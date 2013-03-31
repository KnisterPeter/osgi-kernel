package de.matrixweb.osgi.kernel.maven.impl;

import java.util.List;

/**
 * @author markusw
 */
public interface Dependency {

  /**
   * @return Returns the {@link Pom} for this {@link Dependency}
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
   * @return Returns a list of excluded {@link Pom} from this {@link Dependency}
   */
  List<Pom> getExclusions();

  /**
   * @return Returns the group artifact key (e.g. used for depencency lookup)
   */
  String getGroupArtifactKey();

  /**
   * Updates the {@link Dependency} properties after the containing {@link Pom}
   * parent (if any) has been resolved.
   */
  void updateAfterParentResolved();

}
