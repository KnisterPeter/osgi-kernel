package de.matrixweb.osgi.kernel.maven.impl;

/**
 * @author markusw
 */
public class Dumper {

  /**
   * @param indent
   *          The indention level
   * @param pom
   *          The {@link Pom} to dump
   * @return Returns the pom with all {@link Dependency}s as {@link String}
   */
  public String dump(final int indent, final Pom pom) {
    return dump(indent, new Filter.AcceptAll(), pom);
  }

  /**
   * @param indent
   *          The indention level
   * @param filter
   *          The {@link Filter} to use during dumping
   * @param pom
   *          The {@link Pom} to dump
   * @return Returns the pom with all {@link Dependency}s as {@link String}
   */
  public String dump(final int indent, final Filter filter, final Pom pom) {
    final String padding = createPadding(indent);
    final StringBuilder sb = new StringBuilder(padding).append(pom.toString());
    dumpDependencys(sb, padding, indent, filter, (PomImpl) pom);
    return sb.toString();
  }

  private String dump(final int indent, final Filter filter,
      final Dependency dependency) {
    final String padding = createPadding(indent);
    final StringBuilder sb = new StringBuilder(padding).append(dependency
        .toString());
    dumpDependencys(sb, padding, indent, filter, (PomImpl) dependency.getPom());
    return sb.toString();
  }

  private String createPadding(final int indent) {
    final StringBuilder padding = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      padding.append("  ");
    }
    return padding.toString();
  }

  private void dumpDependencys(final StringBuilder sb, final String padding,
      final int indent, final Filter filter, final PomImpl pom) {
    for (final Dependency dep : pom.getFilteredDependencies(false, filter)) {
      sb.append("\n").append(padding).append(dump(indent + 1, filter, dep));
    }
  }

}
