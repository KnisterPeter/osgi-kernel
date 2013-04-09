package de.matrixweb.osgi.kernel.maven.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author markusw
 */
public final class MavenUtils {

  private MavenUtils() {
  }

  /**
   * @param artifact
   * @return Returns a URN for the given {@link Pom}
   */
  public static String toURN(final Artifact artifact) {
    final StringBuilder sb = new StringBuilder("mvn:")
        .append(artifact.getGroupId()).append(':')
        .append(artifact.getArtifactId()).append(':')
        .append(artifact.getVersion());
    if (!"jar".equals(artifact.getPackagingOrType())) {
      sb.append(':').append(artifact.getPackagingOrType());
    }
    return sb.toString();
  }

  /**
   * @param repository
   * @param artifact
   * @param type
   * @return Returns a url to the given artifact in the given repository
   */
  public static String toUrl(final String repository, final Artifact artifact,
      final String type) {
    return repository + '/' + artifact.getGroupId().replace('.', '/') + '/'
        + artifact.getArtifactId() + '/' + artifact.getVersion() + '/'
        + artifact.getArtifactId() + '-' + artifact.getVersion() + '.' + type;
  }

  /**
   * @param resolver
   * @param pom
   *          The {@link Pom} to dump
   * @return Returns the pom with all {@link Dependency}s as {@link String}
   * @throws ParserConfigurationException
   * @throws IOException
   */
  public static String dump(final PomResolver resolver, final Pom pom)
      throws IOException, ParserConfigurationException {
    return dump(resolver, new Filter.AcceptAll(), pom);
  }

  /**
   * @param resolver
   * @param filter
   *          The {@link Filter} to use during dumping
   * @param pom
   *          The {@link Pom} to dump
   * @return Returns the pom with all {@link Dependency}s as {@link String}
   * @throws ParserConfigurationException
   * @throws IOException
   */
  public static String dump(final PomResolver resolver, final Filter filter,
      final Pom pom) throws IOException, ParserConfigurationException {
    final String padding = createPadding(0);
    final StringBuilder sb = new StringBuilder(padding).append(pom.toString());
    dumpDependencys(sb, padding, 0, resolver, filter, pom);
    return sb.toString();
  }

  private static String dump(final int indent, final PomResolver resolver,
      final Filter filter, final Pom dependency) throws IOException,
      ParserConfigurationException {
    final String padding = createPadding(indent);
    final StringBuilder sb = new StringBuilder(padding).append(dependency
        .toString());
    dumpDependencys(sb, padding, indent, resolver, filter, dependency);
    return sb.toString();
  }

  private static String createPadding(final int indent) {
    final StringBuilder padding = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      padding.append("  ");
    }
    return padding.toString();
  }

  private static void dumpDependencys(final StringBuilder sb,
      final String padding, final int indent, final PomResolver resolver,
      final Filter filter, final Pom pom) throws IOException,
      ParserConfigurationException {
    for (final Dependency dependency : pom.getDependencies()) {
      if (filter.accept(dependency)) {
        final Pom resolved = resolver.resolvePom(dependency);
        if (resolved != null) {
          sb.append("\n").append(padding)
              .append(dump(indent + 1, resolver, filter, resolved));
        }
      }
    }
  }

}
