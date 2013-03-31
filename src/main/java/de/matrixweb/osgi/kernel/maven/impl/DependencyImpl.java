package de.matrixweb.osgi.kernel.maven.impl;

import java.util.List;

/**
 * @author markusw
 */
public class DependencyImpl implements Dependency {

  private final Pom pom;

  private String scope = "runtime";

  private boolean optional = false;

  /**
   * @param pom
   */
  public DependencyImpl(final Pom pom) {
    this.pom = pom;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getPom()
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
    return this.pom.getType();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#setType(java.lang.String)
   */
  @Override
  public void setType(final String type) {
    this.pom.setType(type);
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
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getExclusions()
   */
  @Override
  public List<Pom> getExclusions() {
    throw new UnsupportedOperationException();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#getGroupArtifactKey()
   */
  @Override
  public String getGroupArtifactKey() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.pom.getGroupId()).append(':')
        .append(this.pom.getArtifactId());
    if (!"jar".equals(getType())) {
      sb.append("::").append(getType());
    }
    return sb.toString();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Dependency#updateAfterParentResolved()
   */
  @Override
  public void updateAfterParentResolved() {
    ((PomImpl) getPom()).updateAfterParentResolved(getGroupArtifactKey());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toString()
   */
  @Override
  public String toString() {
    return getPom().toURN() + " [" + getScope() + (isOptional() ? '*' : "")
        + "]";
  }

}
