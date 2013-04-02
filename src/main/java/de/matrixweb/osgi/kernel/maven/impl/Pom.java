package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Collection;
import java.util.Map;

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
   * @param dependency
   *          the dependency to add
   */
  void addDependency(Dependency dependency);

  /**
   * @return the dependencies
   */
  Collection<Dependency> getDependencies();

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
   * @param input
   *          An input string
   * @return Returns the resolved input string
   */
  String resolveProperties(final String input);

}
