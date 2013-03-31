package de.matrixweb.osgi.kernel.maven.impl;

/**
 * @author markusw
 */
public class ArtifactImpl implements Artifact {

  private String groupId;

  private String artifactId;

  private String version;

  /**
   * @deprecated
   */
  @Deprecated
  private String type;

  private Artifact template;

  /**
   * 
   */
  public ArtifactImpl() {
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public ArtifactImpl(final String groupId, final String artifactId,
      final String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  protected ArtifactImpl(final Artifact copy) {
    this.groupId = copy.getGroupId();
    this.artifactId = copy.getArtifactId();
    this.version = copy.getVersion();
    this.type = copy.getType();
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
    String v = this.version;
    if (v == null && this.template != null) {
      v = this.template.getVersion();
    }
    return v;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setVersion(java.lang.String)
   */
  @Override
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getType()
   */
  @Override
  @Deprecated
  public String getType() {
    String t = this.type;
    if (t == null && this.template != null) {
      t = this.template.getType();
    }
    if (t == null) {
      t = "jar";
    }
    return t;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setType(java.lang.String)
   */
  @Deprecated
  @Override
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#clear()
   */
  @Override
  public void clear() {
    this.groupId = null;
    this.artifactId = null;
    this.version = null;
    this.type = null;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#setTemplate(de.matrixweb.osgi.kernel.maven.impl.Artifact)
   */
  @Override
  public void setTemplate(final Artifact template) {
    this.template = template;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#toURN()
   */
  @Override
  public String toURN() {
    final StringBuilder sb = new StringBuilder("mvn:").append(getGroupId())
        .append(':').append(getArtifactId()).append(':').append(getVersion());
    if (!"jar".equals(getType())) {
      sb.append(':').append(getType());
    }
    return sb.toString();
  }

}
