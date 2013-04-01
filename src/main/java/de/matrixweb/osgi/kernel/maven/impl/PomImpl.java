package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author markusw
 */
public class PomImpl extends ArtifactImpl implements Pom {

  // private PomImpl dependant;

  private Pom parent;

  private final Map<String, String> properties = new HashMap<String, String>();

  private final Map<String, Dependency> managedDependencies = new HashMap<String, Dependency>();

  private final Map<String, Dependency> dependencies = new HashMap<String, Dependency>();

  private Artifact template;

  /**
   * 
   */
  public PomImpl() {
    super("jar");
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public PomImpl(final String groupId, final String artifactId,
      final String version) {
    super(groupId, artifactId, version, "jar");
    initProperties();
  }

  /**
   * @param dependant
   * @param copy
   */
  public PomImpl(final Pom dependant, final Pom copy) {
    super(copy);
    // this.dependant = (PomImpl) dependant;
    setPackaging(copy.getPackaging());
    initProperties();
  }

  private void initProperties() {
    addProperty("project.groupId", getGroupId());
    addProperty("pom.groupId", getGroupId());
    addProperty("groupId", getGroupId());
    addProperty("project.artifactId", getArtifactId());
    addProperty("pom.artifactId", getArtifactId());
    addProperty("artifactId", getArtifactId());
    addProperty("project.version", getVersion());
    addProperty("pom.version", getVersion());
    addProperty("version", getVersion());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getGroupId()
   */
  @Override
  public final String getGroupId() {
    return resolveProperties(super.getGroupId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getArtifactId()
   */
  @Override
  public final String getArtifactId() {
    return resolveProperties(super.getArtifactId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getVersion()
   */
  @Override
  public final String getVersion() {
    String v = super.getVersion();
    if (v == null && this.template != null) {
      v = this.template.getVersion();
    }
    return resolveProperties(v);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#resolveProperties(java.lang.String)
   */
  @Override
  public String resolveProperties(final String input) {
    String result = input;
    if (result != null) {
      int start = result.indexOf("${");
      if (start > -1) {
        int pos = 0;
        final StringBuilder sb = new StringBuilder();
        while (start > -1) {
          final int end = result.indexOf('}', start);
          final String match = result.substring(start + 2, end);
          final String replacement = getReplacement(match);
          if (replacement != null) {
            sb.append(result.substring(pos, start)).append(replacement);
          } else {
            sb.append(result.substring(pos, end + 1));
          }
          pos = end + 1;
          start = result.indexOf("${", pos);
        }
        final String done = sb.toString();
        if (!done.equals(result)) {
          result = resolveProperties(sb.toString());
        } else {
          result = done;
        }
      }
    }
    return result;
  }

  protected final String getReplacement(final String name) {
    final String replacement = getProperties().get(name);
    // if (replacement == null && this.dependant != null) {
    // replacement = this.dependant.getReplacement(name);
    // }
    return replacement;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getParent()
   */
  @Override
  public Pom getParent() {
    return this.parent;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#setParent(de.matrixweb.osgi.kernel.maven.impl.Pom)
   */
  @Override
  public void setParent(final Pom parent) {
    this.parent = parent;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getPackaging()
   */
  @Override
  public String getPackaging() {
    return getPackagingOrType();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#setPackaging(java.lang.String)
   */
  @Override
  public final void setPackaging(final String packaging) {
    setPackagingOrType(packaging);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#addManagedDependency(de.matrixweb.osgi.kernel.maven.impl.Dependency)
   */
  @Override
  public void addManagedDependency(final Dependency managedDependency) {
    this.managedDependencies.put(managedDependency.getGroupArtifactKey(),
        managedDependency);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getManagedDependencies()
   */
  @Override
  public Map<String, Dependency> getManagedDependencies() {
    final Map<String, Dependency> deps = new HashMap<String, Dependency>();
    if (getParent() != null) {
      deps.putAll(getParent().getManagedDependencies());
    }
    deps.putAll(this.managedDependencies);
    return deps;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getDependencies()
   */
  @Override
  public Collection<Dependency> getDependencies() {
    final List<Dependency> list = new LinkedList<Dependency>();
    if (getParent() != null) {
      list.addAll(getParent().getDependencies());
    }
    list.addAll(this.dependencies.values());
    return list;
  }

  /**
   * @param dependency
   */
  @Override
  public void addDependency(final Dependency dependency) {
    this.dependencies.put(dependency.getGroupArtifactKey(), dependency);
  }

  void clearDependencies() {
    this.dependencies.clear();
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getProperties()
   */
  @Override
  public final Map<String, String> getProperties() {
    final Map<String, String> props = new HashMap<String, String>();
    if (getParent() != null) {
      props.putAll(getParent().getProperties());
    }
    props.putAll(this.properties);
    return props;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#addProperty(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public final void addProperty(final String name, final String value) {
    this.properties.put(name, value);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toUrl(java.lang.String)
   */
  @Override
  public String toUrl(final String repository) {
    return toUrl(repository, getPackaging());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toUrl(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public String toUrl(final String repository, final String type) {
    return MavenUtils.toUrl(repository, this, type);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    final String urn = MavenUtils.toURN(this);
    result = prime * result + urn.hashCode();
    return result;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PomImpl other = (PomImpl) obj;
    if (!MavenUtils.toURN(this).equals(MavenUtils.toURN(other))) {
      return false;
    }
    return true;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toString()
   */
  @Override
  public String toString() {
    return MavenUtils.toURN(this);
  }

}
