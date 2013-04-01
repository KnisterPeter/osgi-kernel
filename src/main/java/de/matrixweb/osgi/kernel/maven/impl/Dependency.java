package de.matrixweb.osgi.kernel.maven.impl;

import java.util.List;

/**
 * @author markusw
 */
public interface Dependency extends Artifact {

  /**
   * @return Return the {@link Pom} this {@link Dependency} is declared in
   */
  Pom getPom();

  /**
   * @return Returns the type of this {@link Dependency}
   */
  String getType();

  /**
   * @param type
   *          This {@link Dependency}s type
   */
  void setType(String type);

  /**
   * @return Returns the scope of this {@link Dependency}
   */
  String getScope();

  /**
   * @param scope
   *          This {@link Dependency}s scope
   */
  void setScope(String scope);

  /**
   * @return Returns true if this {@link Dependency} is optional, false
   *         otherwise
   */
  boolean isOptional();

  /**
   * @param optional
   *          True if this {@link Dependency} is optional, false otherwise
   */
  void setOptional(boolean optional);

  /**
   * @param exclusion
   *          The {@link Artifact} exclusion
   */
  void addExclusion(Artifact exclusion);

  /**
   * @param artifact
   *          The {@link Artifact} to check
   * @return Returns true if this artifact is excluded in the dependency
   *         delcaration, false otherwise
   */
  boolean excludes(Artifact artifact);

  /**
   * @return Returns a list of excluded {@link Pom} from this {@link Dependency}
   */
  List<Artifact> getExclusions();

}
