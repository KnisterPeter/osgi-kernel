package de.matrixweb.osgi.kernel.maven.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author markusw
 */
public class PomImpl extends ArtifactImpl implements
    de.matrixweb.osgi.kernel.maven.Artifact.Pom {

  private PomImpl parent;

  private final Map<String, String> properties = new HashMap<String, String>();

  private final Map<String, DependencyImpl> managedDependencies = new HashMap<String, DependencyImpl>();

  private final Map<String, DependencyImpl> dependencies = new HashMap<String, DependencyImpl>();

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

  private final void initProperties() {
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
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#getGroupId()
   */
  @Override
  public final String getGroupId() {
    return resolveProperties(super.getGroupId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#getArtifactId()
   */
  @Override
  public final String getArtifactId() {
    return resolveProperties(super.getArtifactId());
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#getVersion()
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
  public PomImpl getParent() {
    return this.parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(final PomImpl parent) {
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
  public void addManagedDependency(final DependencyImpl managedDependency) {
    this.managedDependencies.put(managedDependency.getGroupArtifactKey(),
        managedDependency);
  }

  /**
   * @return the managedDependencies
   */
  public Map<String, DependencyImpl> getManagedDependencies() {
    final Map<String, DependencyImpl> deps = new HashMap<String, DependencyImpl>();
    if (getParent() != null) {
      deps.putAll(getParent().getManagedDependencies());
    }
    deps.putAll(this.managedDependencies);
    return deps;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Artifact.Pom#getDependencies()
   */
  @Override
  public Collection<DependencyImpl> getDependencies() {
    final List<DependencyImpl> list = new LinkedList<DependencyImpl>();
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
  public void addDependency(final DependencyImpl dependency) {
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
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    final String urn = toURN();
    result = prime * result + urn.hashCode();
    return result;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#equals(java.lang.Object)
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
    if (!toURN().equals(other.toURN())) {
      return false;
    }
    return true;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.PomImpl#toString()
   */
  @Override
  public String toString() {
    return toURN();
  }

}
