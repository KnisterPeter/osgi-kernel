package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author markusw
 */
public class Pom extends Artifact {

  private Pom parent;

  private final Map<String, String> properties = new HashMap<String, String>();

  private final Map<String, Dependency> managedDependencies = new HashMap<String, Dependency>();

  private final Map<String, Dependency> dependencies = new HashMap<String, Dependency>();

  /**
   * 
   */
  public Pom() {
    super("jar");
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public Pom(final String groupId, final String artifactId, final String version) {
    super(groupId, artifactId, version, "jar");
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
    return resolveProperties(super.getVersion());
  }

  /**
   * @param input
   *          An input string
   * @return Returns the resolved input string
   */
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
    return getProperties().get(name);
  }

  /**
   * @return the parent
   */
  public Pom getParent() {
    return this.parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(final Pom parent) {
    this.parent = parent;
  }

  /**
   * @return the packaging
   */
  public String getPackaging() {
    return getPackagingOrType();
  }

  /**
   * @param packaging
   *          the packaging to set
   */
  public final void setPackaging(final String packaging) {
    setPackagingOrType(packaging);
  }

  /**
   * @param managedDependency
   *          the managedDependency to set
   */
  public void addManagedDependency(final Dependency managedDependency) {
    this.managedDependencies.put(managedDependency.getGroupArtifactKey(),
        managedDependency);
  }

  /**
   * @return the managedDependencies
   */
  public Map<String, Dependency> getManagedDependencies() {
    final Map<String, Dependency> deps = new HashMap<String, Dependency>();
    if (getParent() != null) {
      deps.putAll(getParent().getManagedDependencies());
    }
    deps.putAll(this.managedDependencies);
    return deps;
  }

  /**
   * @return the dependencies
   */
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
   *          the dependency to add
   */
  public void addDependency(final Dependency dependency) {
    this.dependencies.put(dependency.getGroupArtifactKey(), dependency);
  }

  /**
   * @return the properties
   */
  public final Map<String, String> getProperties() {
    final Map<String, String> props = new HashMap<String, String>();
    if (getParent() != null) {
      props.putAll(getParent().getProperties());
    }
    props.putAll(this.properties);
    return props;
  }

  /**
   * @param name
   * @param value
   */
  public final void addProperty(final String name, final String value) {
    this.properties.put(name, value);
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
    final Pom other = (Pom) obj;
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
