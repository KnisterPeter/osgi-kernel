package de.matrixweb.osgi.kernel.maven.impl;

/**
 * @author markusw
 */
public interface Artifact {

  /**
   * @return the groupId
   */
  String getGroupId();

  /**
   * @param groupId
   *          the groupId to set
   */
  void setGroupId(String groupId);

  /**
   * @return the artifactId
   */
  String getArtifactId();

  /**
   * @param artifactId
   *          the artifactId to set
   */
  void setArtifactId(String artifactId);

  /**
   * @return the version
   */
  String getVersion();

  /**
   * @param version
   *          the version to set
   */
  void setVersion(String version);

  /**
   * @return the type
   * @deprecated An {@link Artifact} does not have a type. A {@link Dependency}
   *             has
   */
  @Deprecated
  String getType();

  /**
   * @param type
   *          the type to set
   * @deprecated An {@link Artifact} does not have a type. A {@link Dependency}
   *             has
   */
  @Deprecated
  void setType(String type);

  /**
   * 
   */
  void clear();

  /**
   * @param template
   *          the template to set
   */
  void setTemplate(Artifact template);

  /**
   * @return Returns the urn for this artifact
   */
  String toURN();

}
