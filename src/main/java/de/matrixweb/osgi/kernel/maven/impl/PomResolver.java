package de.matrixweb.osgi.kernel.maven.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author markusw
 */
public class PomResolver {

  private static final SAXParserFactory PARSER_FACTORY = SAXParserFactory
      .newInstance();

  private final Map<String, Pom> current = new HashMap<String, Pom>();

  private final String repository;

  /**
   * @param repository
   */
  public PomResolver(final String repository) {
    this.repository = repository;
  }

  /**
   * @param pom
   * @return Returns the given {@link Pom} in resolved status
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Pom resolvePom(final Pom pom) throws IOException,
      ParserConfigurationException {
    return resolvePom(pom, null);
  }

  /**
   * @param pom
   * @param input
   * @return Returns the given {@link Pom} in resolved status
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Pom resolvePom(final Pom pom, final InputStream input)
      throws IOException, ParserConfigurationException {
    if (this.current.containsKey(pom.toURN())) {
      // Fast-Return recursive dependency declarations (managed dependencies)
      return this.current.get(pom.toURN());
    }
    this.current.put(pom.toURN(), pom);
    try {
      InputStream is;
      if (input == null) {
        is = new URL(pom.toUrl(this.repository, "pom")).openStream();
      } else {
        is = input;
      }
      try {
        PARSER_FACTORY.newSAXParser().parse(is, new PomParser(pom));
        if (pom.getParent() != null) {
          pom.setParent(resolvePom(pom.getParent()));
        }
        resolveDependencies(pom);
        this.current.remove(pom.toURN());
      } finally {
        is.close();
      }
    } catch (final SAXException e) {
      // Skipping invalid pom
      System.out.println("Invalid pom " + pom.toURN() + " ... skipping");
      this.current.remove(pom.toURN());
    } catch (final FileNotFoundException e) {
      // Skipping missing pom
      this.current.remove(pom.toURN());
    }
    return pom;
  }

  private void resolveDependencies(final Pom pom) throws IOException,
      ParserConfigurationException {
    if (pom instanceof PomImpl) {
      final List<Dependency> list = new ArrayList<Dependency>(
          pom.getDependencies());
      ((PomImpl) pom).clearDependencies();
      for (final Dependency dependency : list) {
        dependency.updateAfterParentResolved();
        pom.addDependency(new DependencyImpl(resolvePom(dependency.getPom())));
      }
    }
  }

}
