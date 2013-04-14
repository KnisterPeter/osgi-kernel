package de.matrixweb.osgi.kernel.maven.impl;

import de.matrixweb.osgi.kernel.maven.Artifact;

/**
 * @author markusw
 */
public class ArtifactImpl implements Artifact {

  private String groupId;

  private String artifactId;

  private String version;

  private String packagingOrType;

  /**
   * @param packagingOrType
   */
  public ArtifactImpl(final String packagingOrType) {
    this.packagingOrType = packagingOrType;
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   * @param packagingOrType
   */
  public ArtifactImpl(final String groupId, final String artifactId,
      final String version, final String packagingOrType) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.packagingOrType = packagingOrType;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact#getGroupId()
   */
  @Override
  public String getGroupId() {
    return this.groupId;
  }

  /**
   * @param groupId
   *          the groupId to set
   */
  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact#getArtifactId()
   */
  @Override
  public String getArtifactId() {
    return this.artifactId;
  }

  /**
   * @param artifactId
   *          the artifactId to set
   */
  public void setArtifactId(final String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact#getVersion()
   */
  @Override
  public String getVersion() {
    return this.version;
  }

  /**
   * @param version
   *          the version to set
   */
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact#getPackagingOrType()
   */
  @Override
  public String getPackagingOrType() {
    return this.packagingOrType;
  }

  /**
   * @param packagingOrType
   *          the packagingOrType to set
   */
  public void setPackagingOrType(final String packagingOrType) {
    this.packagingOrType = packagingOrType;
  }

  /**
   * @return Returns the group artifact key (e.g. used for depencency lookup)
   */
  public String getGroupArtifactKey() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getGroupId()).append(':').append(getArtifactId());
    if (!"jar".equals(getPackagingOrType())) {
      sb.append("::").append(getPackagingOrType());
    }
    return sb.toString();
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ArtifactImpl[" + getGroupId() + ':' + getArtifactId() + ':'
        + getVersion() + ':' + getPackagingOrType() + ']';
  }

}
