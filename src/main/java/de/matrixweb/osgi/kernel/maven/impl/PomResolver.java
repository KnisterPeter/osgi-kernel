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

import de.matrixweb.osgi.kernel.utils.Logger;

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
        final Pom pom = new Pom(artifact.getGroupId(),
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
      Logger.log("Invalid pom " + MavenUtils.toURN(artifact) + " ... skipping");
    } catch (final FileNotFoundException e) {
      // Skipping missing pom
      Logger.log("Missing pom " + MavenUtils.toURN(artifact) + " ... skipping");
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
    inProcess.add(new DependencyPair(null, null, pom));
    while (!inProcess.isEmpty()) {
      final DependencyPair pair = inProcess.poll();
      for (final Dependency dependency : pair.pom.getDependencies()) {
        if (!done.contains(dependency.getGroupArtifactKey())
            && filter.accept(dependency) && !pair.excludes(dependency)) {
          final Pom resolved = resolvePom(dependency);
          if (resolved != null) {
            done.add(dependency.getGroupArtifactKey());
            set.add(resolved);
            inProcess.add(new DependencyPair(pair, dependency, resolved));
          }
        }
      }
    }

    return set;
  }

  private static class DependencyPair {

    private final DependencyPair parent;

    private final Dependency dependency;

    private final Pom pom;

    DependencyPair(final DependencyPair parent, final Dependency dependency,
        final Pom pom) {
      this.parent = parent;
      this.dependency = dependency;
      this.pom = pom;
    }

    boolean excludes(final Artifact artifact) {
      return this.dependency != null && this.dependency.excludes(artifact)
          || this.parent != null && this.parent.excludes(artifact);
    }

  }

}
