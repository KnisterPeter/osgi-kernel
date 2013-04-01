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
   * @return Return packaging or type
   */
  String getPackagingOrType();

  /**
   * @param packagingOrType
   *          the packagingOrType to set
   */
  void setPackagingOrType(String packagingOrType);

  /**
   * @return Returns the group artifact key (e.g. used for depencency lookup)
   */
  String getGroupArtifactKey();

}
