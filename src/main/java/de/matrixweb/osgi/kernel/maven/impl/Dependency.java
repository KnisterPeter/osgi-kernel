package de.matrixweb.osgi.kernel.maven.impl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author markusw
 */
public class Dependency extends Artifact {

  private final Pom pom;

  private String scope = "runtime";

  private boolean optional = false;

  private final List<Artifact> exclusions = new LinkedList<Artifact>();

  /**
   * @param pom
   *          The {@link Pom} this {@link Dependency} is declared in
   */
  public Dependency(final Pom pom) {
    super("jar");
    this.pom = pom;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getGroupId()
   */
  @Override
  public String getGroupId() {
    return getPom().resolveProperties(super.getGroupId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getArtifactId()
   */
  @Override
  public String getArtifactId() {
    return getPom().resolveProperties(super.getArtifactId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Artifact#getVersion()
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
   * @return Return the {@link Pom} this {@link Dependency} is declared in
   */
  public Pom getPom() {
    return this.pom;
  }

  /**
   * @return Returns the type of this {@link Dependency}
   */
  public String getType() {
    return getPackagingOrType();
  }

  /**
   * @param type
   *          This {@link Dependency}s type
   */
  public void setType(final String type) {
    setPackagingOrType(type);
  }

  /**
   * @return Returns the scope of this {@link Dependency}
   */
  public String getScope() {
    return this.scope;
  }

  /**
   * @param scope
   *          This {@link Dependency}s scope
   */
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * @return Returns true if this {@link Dependency} is optional, false
   *         otherwise
   */
  public boolean isOptional() {
    return this.optional;
  }

  /**
   * @param optional
   *          True if this {@link Dependency} is optional, false otherwise
   */
  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  /**
   * @param exclusion
   *          The {@link Artifact} exclusion
   */
  public void addExclusion(final Artifact exclusion) {
    this.exclusions.add(exclusion);
  }

  /**
   * @param artifact
   *          The {@link Artifact} to check
   * @return Returns true if this artifact is excluded in the dependency
   *         delcaration, false otherwise
   */
  public boolean excludes(final Artifact artifact) {
    for (final Artifact excl : getExclusions()) {
      if (excl.getGroupArtifactKey().equals(artifact.getGroupArtifactKey())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return Returns a list of excluded {@link Pom} from this {@link Dependency}
   */
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
