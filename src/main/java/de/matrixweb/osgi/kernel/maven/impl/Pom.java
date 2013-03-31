package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author markusw
 */
public interface Pom extends Artifact {

  /**
   * @return the parent
   */
  Pom getParent();

  /**
   * @param parent
   *          the parent to set
   */
  void setParent(Pom parent);

  /**
   * @return the packaging
   */
  String getPackaging();

  /**
   * @param packaging
   *          the packaging to set
   */
  void setPackaging(String packaging);

  /**
   * @param managedDependency
   *          the managedDependency to set
   */
  void addManagedDependency(Dependency managedDependency);

  /**
   * @return the managedDependencies
   */
  Map<String, Dependency> getManagedDependencies();

  /**
   * TODO: This should be done in the resolver not in the {@link Pom}
   * 
   * @param filter
   * @return Returns a {@link Set} of {@link Pom}s which are the nearest
   *         dependencies of this {@link Pom}
   */
  Set<Dependency> resolveNearestDependencies(Filter filter);

  /**
   * @param dependency
   *          the dependency to add
   */
  void addDependency(Dependency dependency);

  /**
   * @return the dependencies
   */
  Collection<Dependency> getDependencies();

  /**
   * TODO: Exclusions belong to the dependency not the {@link Pom}
   * 
   * @param exclusion
   *          the exclusion to add
   */
  void addExclusion(String exclusion);

  /**
   * TODO: Exclusions belong to the dependency not the {@link Pom}
   * 
   * @return Returns the exclusions
   */
  List<String> getExclusions();

  /**
   * @return the properties
   */
  Map<String, String> getProperties();

  /**
   * @param name
   * @param value
   */
  void addProperty(String name, String value);

  /**
   * @param repository
   * @return Returns the {@link Pom} {@link java.net.URL} locating it in the
   *         repository
   */
  String toUrl(String repository);

  /**
   * @param repository
   * @param type
   * @return Returns the {@link Pom} {@link java.net.URL} locating it in the
   *         repository
   */
  String toUrl(String repository, String type);

}
