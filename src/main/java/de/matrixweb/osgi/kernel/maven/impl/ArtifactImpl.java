package de.matrixweb.osgi.kernel.maven.impl;

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
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getGroupId()
   */
  @Override
  public String getGroupId() {
    return this.groupId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setGroupId(java.lang.String)
   */
  @Override
  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getArtifactId()
   */
  @Override
  public String getArtifactId() {
    return this.artifactId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setArtifactId(java.lang.String)
   */
  @Override
  public void setArtifactId(final String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getVersion()
   */
  @Override
  public String getVersion() {
    return this.version;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setVersion(java.lang.String)
   */
  @Override
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * @return the packagingOrType
   */
  @Override
  public String getPackagingOrType() {
    return this.packagingOrType;
  }

  /**
   * @param packagingOrType
   *          the packagingOrType to set
   */
  @Override
  public void setPackagingOrType(final String packagingOrType) {
    this.packagingOrType = packagingOrType;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getGroupArtifactKey()
   */
  @Override
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
    return "Artifact[" + getGroupId() + ':' + getArtifactId() + ':'
        + getVersion() + ':' + getPackagingOrType() + ']';
  }

}
