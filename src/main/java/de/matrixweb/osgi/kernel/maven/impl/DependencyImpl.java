package de.matrixweb.osgi.kernel.maven.impl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author markusw
 */
public class DependencyImpl extends ArtifactImpl implements Dependency {

  private final Pom pom;

  private String scope = "runtime";

  private boolean optional = false;

  private final List<Artifact> exclusions = new LinkedList<Artifact>();

  /**
   * @param pom
   *          The {@link Pom} this {@link Dependency} is declared in
   */
  public DependencyImpl(final Pom pom) {
    super("jar");
    this.pom = pom;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.ArtifactImpl#getGroupId()
   */
  @Override
  public String getGroupId() {
    return getPom().resolveProperties(super.getGroupId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.ArtifactImpl#getArtifactId()
   */
  @Override
  public String getArtifactId() {
    return getPom().resolveProperties(super.getArtifactId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.ArtifactImpl#getVersion()
   */
  @Override
  public String getVersion() {
    String v = super.getVersion();
    if (v == null) {
      v = getPom().getManagedDependencies().get(getGroupArtifactKey())
          .getVersion();
    }
    return getPom().resolveProperties(v);
  }

  /**
   * @return the pom
   */
  @Override
  public Pom getPom() {
    return this.pom;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getType()
   */
  @Override
  public String getType() {
    return getPackagingOrType();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#setType(java.lang.String)
   */
  @Override
  public void setType(final String type) {
    setPackagingOrType(type);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getScope()
   */
  @Override
  public String getScope() {
    return this.scope;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#setScope(java.lang.String)
   */
  @Override
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#isOptional()
   */
  @Override
  public boolean isOptional() {
    return this.optional;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#setOptional(boolean)
   */
  @Override
  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#addExclusion(de.matrixweb.osgi.kernel.maven.impl.Artifact)
   */
  @Override
  public void addExclusion(final Artifact exclusion) {
    this.exclusions.add(exclusion);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#excludes(de.matrixweb.osgi.kernel.maven.impl.Artifact)
   */
  @Override
  public boolean excludes(final Artifact artifact) {
    for (final Artifact excl : getExclusions()) {
      if (excl.getGroupArtifactKey().equals(artifact.getGroupArtifactKey())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getExclusions()
   */
  @Override
  public List<Artifact> getExclusions() {
    return this.exclusions;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toString()
   */
  @Override
  public String toString() {
    return MavenUtils.toURN(this) + " [" + getScope()
        + (isOptional() ? '*' : "") + "]";
  }

}
