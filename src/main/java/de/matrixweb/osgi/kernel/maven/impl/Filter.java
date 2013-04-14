package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Arrays;
import java.util.List;

import de.matrixweb.osgi.kernel.maven.Artifact.Dependency;

/**
 * @author markusw
 */
public interface Filter {

  /**
   * @param dependency
   *          The {@link Dependency} to apply the filter on
   * @return Returns true if the given {@link Dependency} should be included,
   *         false otherwise.
   */
  boolean accept(Dependency dependency);

  /** */
  class CompoundFilter implements Filter {

    private final List<Filter> filters;

    /**
     * @param filters
     */
    public CompoundFilter(final Filter... filters) {
      this.filters = Arrays.asList(filters);
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      boolean accept = true;
      for (final Filter filter : filters) {
        accept &= filter.accept(dependency);
      }
      return accept;
    }

  }

  /** */
  class AcceptAll implements Filter {

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      return true;
    }

  }

  /** */
  class AcceptScopes implements Filter {

    private final List<String> scopes;

    /**
     * @param scopes
     */
    public AcceptScopes(final String... scopes) {
      this.scopes = Arrays.asList(scopes);
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      return scopes.contains(dependency.getScope());
    }

  }

  /** */
  class AcceptTypes implements Filter {

    private final List<String> types;

    /**
     * @param types
     */
    public AcceptTypes(final String... types) {
      this.types = Arrays.asList(types);
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      return types.contains(dependency.getType());
    }

  }

  /** */
  class NotAcceptTypes implements Filter {

    private final List<String> types;

    /**
     * @param types
     */
    public NotAcceptTypes(final String... types) {
      this.types = Arrays.asList(types);
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      return !types.contains(dependency.getType());
    }

  }

  /** */
  class AcceptOptional implements Filter {

    private final Boolean optional;

    /**
     * @param optional
     */
    public AcceptOptional(final boolean optional) {
      this.optional = optional;
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.impl.Filter#accept(de.matrixweb.osgi.kernel.maven.Artifact.Dependency)
     */
    @Override
    public boolean accept(final Dependency dependency) {
      return optional.equals(dependency.isOptional());
    }

  }

}
