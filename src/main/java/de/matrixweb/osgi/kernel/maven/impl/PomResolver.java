package de.matrixweb.osgi.kernel.maven.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author markusw
 */
public class PomResolver {

  private static final SAXParserFactory PARSER_FACTORY = SAXParserFactory
      .newInstance();

  private final String repository;

  private final Map<String, Pom> resolved = new HashMap<String, Pom>();

  /**
   * @param repository
   */
  public PomResolver(final String repository) {
    this.repository = repository;
  }

  /**
   * @param artifact
   * @return Returns the given {@link Pom} in resolved status
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Pom resolvePom(final Artifact artifact) throws IOException,
      ParserConfigurationException {
    return resolvePom(artifact, null);
  }

  /**
   * @param artifact
   * @param input
   * @return Returns the given {@link Pom} in resolved status
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Pom resolvePom(final Artifact artifact, final InputStream input)
      throws IOException, ParserConfigurationException {
    final String urn = MavenUtils.toURN(artifact);
    if (!this.resolved.containsKey(urn)) {
      this.resolved.put(urn, internalResolve(artifact, input));
    }
    return this.resolved.get(urn);
  }

  private Pom internalResolve(final Artifact artifact, final InputStream input)
      throws IOException, ParserConfigurationException {
    try {
      InputStream is;
      if (input == null) {
        is = new URL(MavenUtils.toUrl(this.repository, artifact, "pom"))
            .openStream();
      } else {
        is = input;
      }
      try {
        final Pom pom = new PomImpl(artifact.getGroupId(),
            artifact.getArtifactId(), artifact.getVersion());
        PARSER_FACTORY.newSAXParser().parse(is, new PomParser(pom));
        if (pom.getParent() != null) {
          pom.setParent(internalResolve(pom.getParent(), null));
        }
        return pom;
      } finally {
        is.close();
      }
    } catch (final SAXException e) {
      // Skipping invalid pom
      System.out.println("Invalid pom " + MavenUtils.toURN(artifact)
          + " ... skipping");
    } catch (final FileNotFoundException e) {
      // Skipping missing pom
      System.out.println("Missing pom " + MavenUtils.toURN(artifact)
          + " ... skipping");
    }
    return null;
  }

  /**
   * @param pom
   *          The {@link Pom} to the get all dependencies for
   * @param filter
   *          The {@link Filter} to apply to the dependencies
   * @return Returns a set of all dependencies for the given {@link Pom}
   * @throws ParserConfigurationException
   * @throws IOException
   */
  public Set<Pom> getFilteredDependencies(final Pom pom, final Filter filter)
      throws IOException, ParserConfigurationException {
    final Set<Pom> set = new HashSet<Pom>();

    final Set<String> done = new HashSet<String>();
    final Queue<DependencyPair> inProcess = new ConcurrentLinkedQueue<DependencyPair>();
    inProcess.add(new DependencyPair(null, pom));
    while (!inProcess.isEmpty()) {
      final DependencyPair current = inProcess.poll();
      for (final Dependency dependency : current.pom.getDependencies()) {
        if (!done.contains(dependency.getGroupArtifactKey())
            && filter.accept(dependency)
            && !excludesArtifact(current, dependency)) {
          final Pom resolved = resolvePom(dependency);
          if (resolved != null) {
            done.add(dependency.getGroupArtifactKey());
            set.add(resolved);
            inProcess.add(new DependencyPair(dependency, resolved));
          }
        }
      }
    }

    return set;
  }

  private boolean excludesArtifact(final DependencyPair pair,
      final Dependency dependency) {
    return pair.dependency != null && pair.dependency.excludes(dependency);
  }

  private static class DependencyPair {

    private final Dependency dependency;

    private final Pom pom;

    DependencyPair(final Dependency dependency, final Pom pom) {
      this.dependency = dependency;
      this.pom = pom;
    }

  }

}
