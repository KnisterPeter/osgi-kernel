package de.matrixweb.osgi.kernel.maven.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author markusw
 */
public class PomImpl extends ArtifactImpl implements Pom {

  private PomImpl dependant;

  private Pom parent;

  private String packaging = "jar";

  private final Map<String, String> properties = new HashMap<String, String>();

  private final Map<String, Dependency> managedDependencies = new HashMap<String, Dependency>();

  private final Map<String, Dependency> dependencies = new HashMap<String, Dependency>();

  /**
   * TODO: Move to {@link Dependency}
   */
  @Deprecated
  private final List<String> exclusions = new LinkedList<String>();

  /**
   * 
   */
  public PomImpl() {
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public PomImpl(final String groupId, final String artifactId,
      final String version) {
    super(groupId, artifactId, version);
    initProperties();
  }

  /**
   * @param dependant
   * @param copy
   */
  public PomImpl(final Pom dependant, final Pom copy) {
    super(copy);
    this.dependant = (PomImpl) dependant;
    setPackaging(copy.getPackaging());
    this.exclusions.addAll(copy.getExclusions());
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

  void updateAfterParentResolved(final String gak) {
    if (getVersion() == null && this.dependant != null) {
      final Artifact artifact = this.dependant.getManagedDependencies()
          .get(gak).getPom();
      setTemplate(artifact);
    }
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

  private String resolveProperties(final String input) {
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
    String replacement = getProperties().get(name);
    if (replacement == null && this.dependant != null) {
      replacement = this.dependant.getReplacement(name);
    }
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
    return this.packaging;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#setPackaging(java.lang.String)
   */
  @Override
  public final void setPackaging(final String packaging) {
    this.packaging = packaging;
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
    return this.dependencies.values();
  }

  Set<Dependency> getFilteredDependencies(final boolean transitive,
      final Filter filter) {
    final Set<Dependency> set = new HashSet<Dependency>();
    set.addAll(internalGetFilteredDependencies(transitive,
        transitive ? new Filter.CompoundFilter(filter,
            new Filter.AcceptOptional(false)) : filter));
    return set;
  }

  private Set<Dependency> internalGetFilteredDependencies(
      final boolean transitive, final Filter filter) {
    final Set<Dependency> set = new HashSet<Dependency>();
    final List<String> excl = getAllExclusions();
    for (final Dependency dependency : getDependenciesIncludingParent()) {
      if (!excl.contains(dependency.getGroupArtifactKey())
          && filter.accept(dependency)) {
        set.add(dependency);
        if (transitive) {
          set.addAll(((PomImpl) dependency.getPom())
              .internalGetFilteredDependencies(transitive, filter));
        }
      }
    }
    set.removeAll(excl);
    return set;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#resolveNearestDependencies(de.matrixweb.osgi.kernel.maven.impl.Filter)
   */
  @Override
  public Set<Dependency> resolveNearestDependencies(final Filter filter) {
    final Set<Dependency> set = new HashSet<Dependency>();
    for (final Dependency candidate : getFilteredDependencies(true, filter)) {
      final Queue<Dependency> nodes = new ConcurrentLinkedQueue<Dependency>();
      nodes.addAll(getDependencies());
      final Dependency result = findNearestDependency(nodes, candidate);
      if (result != null && filter.accept(result)) {
        set.add(result);
      }
    }
    return removeNearestExclusions(set);
  }

  private Set<Dependency> removeNearestExclusions(
      final Set<Dependency> dependencies) {
    final Set<String> excl = new HashSet<String>();
    for (final Dependency dependency : dependencies) {
      excl.addAll(((PomImpl) dependency.getPom()).getAllExclusions());
    }
    final Iterator<Dependency> it = dependencies.iterator();
    while (it.hasNext()) {
      if (excl.contains(it.next().getGroupArtifactKey())) {
        it.remove();
      }
    }
    return dependencies;
  }

  private Dependency findNearestDependency(final Queue<Dependency> nodes,
      final Dependency candidate) {
    while (!nodes.isEmpty()) {
      final Dependency node = nodes.remove();
      if (candidate.getGroupArtifactKey().equals(node.getGroupArtifactKey())) {
        return node;
      }
      for (final Dependency dependency : ((PomImpl) node.getPom())
          .getDependenciesIncludingParent()) {
        if (dependency != node) {
          nodes.add(dependency);
        }
      }
    }
    return null;
  }

  private List<Dependency> getDependenciesIncludingParent() {
    final List<Dependency> list = new ArrayList<Dependency>();
    list.addAll(getDependencies());
    if (getParent() != null) {
      list.addAll(getParent().getDependencies());
    }
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

  @Deprecated
  private List<String> getAllExclusions() {
    final List<String> list = new ArrayList<String>();
    list.addAll(this.exclusions);
    if (this.dependant != null) {
      list.addAll(this.dependant.getAllExclusions());
    }
    return list;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#addExclusion(java.lang.String)
   */
  @Deprecated
  @Override
  public void addExclusion(final String exclusion) {
    this.exclusions.add(exclusion);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#getExclusions()
   */
  @Deprecated
  @Override
  public List<String> getExclusions() {
    return this.exclusions;
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
    return repository + '/' + getGroupId().replace('.', '/') + '/'
        + getArtifactId() + '/' + getVersion() + '/' + getArtifactId() + '-'
        + getVersion() + '.' + type;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#hashCode()
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
    if (!toURN().equals(other.toURN())) {
      return false;
    }
    return true;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.impl.Pom#toString()
   */
  @Override
  public String toString() {
    return toURN();
  }

}
