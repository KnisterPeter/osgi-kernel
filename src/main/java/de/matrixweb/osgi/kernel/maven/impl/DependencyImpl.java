package de.matrixweb.osgi.kernel.maven.impl;

import java.util.LinkedList;
import java.util.List;

import de.matrixweb.osgi.kernel.maven.Artifact.Dependency;

/**
 * @author markusw
 */
public class DependencyImpl extends ArtifactImpl implements Dependency {

  private final PomImpl pom;

  private String scope = "runtime";

  private boolean optional = false;

  private final List<ArtifactImpl> exclusions = new LinkedList<ArtifactImpl>();

  /**
   * @param pom
   *          The {@link PomImpl} this {@link DependencyImpl} is declared in
   */
  public DependencyImpl(final PomImpl pom) {
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
   * @return Return the {@link PomImpl} this {@link DependencyImpl} is declared
   *         in
   */
  public PomImpl getPom() {
    return this.pom;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact.Dependency#getType()
   */
  @Override
  public String getType() {
    return getPackagingOrType();
  }

  /**
   * @param type
   *          This {@link DependencyImpl}s type
   */
  public void setType(final String type) {
    setPackagingOrType(type);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact.Dependency#getScope()
   */
  @Override
  public String getScope() {
    return this.scope;
  }

  /**
   * @param scope
   *          This {@link DependencyImpl}s scope
   */
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact.Dependency#isOptional()
   */
  @Override
  public boolean isOptional() {
    return this.optional;
  }

  /**
   * @param optional
   *          True if this {@link DependencyImpl} is optional, false otherwise
   */
  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  /**
   * @param exclusion
   *          The {@link ArtifactImpl} exclusion
   */
  public void addExclusion(final ArtifactImpl exclusion) {
    this.exclusions.add(exclusion);
  }

  /**
   * @param artifact
   *          The {@link ArtifactImpl} to check
   * @return Returns true if this artifact is excluded in the dependency
   *         delcaration, false otherwise
   */
  public boolean excludes(final ArtifactImpl artifact) {
    for (final ArtifactImpl excl : getExclusions()) {
      if (excl.getGroupArtifactKey().equals(artifact.getGroupArtifactKey())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return Returns a list of excluded {@link PomImpl} from this
   *         {@link DependencyImpl}
   */
  public List<ArtifactImpl> getExclusions() {
    return this.exclusions;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#toString()
   */
  @Override
  public String toString() {
    return toURN() + " [" + getScope() + (isOptional() ? '*' : "") + "]";
  }

}
