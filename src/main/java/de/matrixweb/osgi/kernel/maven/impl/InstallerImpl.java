package de.matrixweb.osgi.kernel.maven.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.FrameworkWiring;

import de.matrixweb.osgi.kernel.maven.Artifact.Pom;
import de.matrixweb.osgi.kernel.maven.MavenInstaller;
import de.matrixweb.osgi.kernel.utils.Logger;

/**
 * @author markusw
 */
public class InstallerImpl implements MavenInstaller {

  private final String repository;

  private final Framework framework;

  /**
   * @param repository
   * @param framework
   */
  public InstallerImpl(final String repository, final Framework framework) {
    this.repository = repository;
    this.framework = framework;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#installOrUpdate(java.lang.String)
   */
  @Override
  public Collection<BundleTask> installOrUpdate(final String mvnURN)
      throws IOException {
    return installOrUpdate(true, mvnURN);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#installOrUpdate(boolean,
   *      java.lang.String)
   */
  @Override
  public Collection<BundleTask> installOrUpdate(final boolean update,
      final String mvnURN) throws IOException {
    return installOrUpdate(true, true, mvnURN);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#installOrUpdate(boolean,
   *      boolean, java.lang.String)
   */
  @Override
  public Collection<BundleTask> installOrUpdate(final boolean update,
      final boolean refresh, final String mvnURN) throws IOException {
    try {
      return startOrUpdate(install(mvnURN), update, refresh);
    } catch (final BundleException e) {
      Logger.log(e);
    }
    return null;
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#installOrUpdate(boolean,
   *      java.io.File[])
   */
  @Override
  public Collection<BundleTask> installOrUpdate(final boolean update,
      final File... files) throws IOException {
    return installOrUpdate(update, true, files);
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#installOrUpdate(boolean,
   *      boolean, java.io.File[])
   */
  @Override
  public Collection<BundleTask> installOrUpdate(final boolean update,
      final boolean refresh, final File... files) throws IOException {
    final Set<BundleTask> tasks = new HashSet<InstallerImpl.BundleTask>();
    for (final File file : files) {
      if (file.getName().endsWith(".jar")) {
        final JarFile jar = new JarFile(file);
        try {
          final Set<BundleTask> result = installFromJarFile(jar);
          tasks.addAll(result);
          if (result.isEmpty()) {
            // Fallback to just install file
            try {
              tasks.add(installNonMavenBundle(file));
            } catch (final BundleException e) {
              Logger.log(e);
            }
          }
        } finally {
          jar.close();
        }
      }
    }
    return startOrUpdate(tasks, update, refresh);
  }

  private BundleTask installNonMavenBundle(final File file)
      throws BundleException {
    final String location = file.toURI().toString();
    final BundleTask task = new BundleTask();
    task.bundle = this.framework.getBundleContext().getBundle(location);
    if (task.bundle == null) {
      task.bundle = this.framework.getBundleContext().installBundle(location);
      task.installed = true;
    }
    return task;
  }

  private Set<BundleTask> installFromJarFile(final JarFile jar)
      throws IOException {
    InputStream input = null;
    PomImpl pom = null;
    final Enumeration<JarEntry> entries = jar.entries();
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      if (entry.getName().endsWith("pom.xml")) {
        input = jar.getInputStream(entry);
      }
      if (entry.getName().endsWith("pom.properties")) {
        final InputStream is = jar.getInputStream(entry);
        try {
          final Properties props = new Properties();
          props.load(is);
          pom = new PomImpl(props.getProperty("groupId"),
              props.getProperty("artifactId"), props.getProperty("version"));
        } finally {
          is.close();
        }
      }
    }
    if (pom != null && input != null) {
      try {
        return install(pom, input);
      } catch (final BundleException e) {
        Logger.log(e);
      }
    }
    if (input != null) {
      input.close();
    }
    return Collections.emptySet();
  }

  /**
   * @param mvnURN
   * @return Returns a {@link Set} of {@link BundleTask}s to process after
   *         installation
   * @throws BundleException
   * @throws IOException
   */
  public Set<BundleTask> install(final String mvnURN) throws BundleException,
      IOException {
    final String[] parts = mvnURN.split(":");
    if ("mvn".equals(parts[0])) {
      final PomImpl pom = new PomImpl(parts[1], parts[2], parts[3]);
      return install(pom, null);
    }
    return Collections.emptySet();
  }

  private Set<BundleTask> install(final PomImpl pom, final InputStream input)
      throws BundleException, IOException {
    final Set<BundleTask> tasks = new HashSet<InstallerImpl.BundleTask>();
    try {
      final PomResolver resolver = new PomResolver(this.repository);
      final PomImpl rpom = resolver.resolvePom(pom, input);
      tasks.add(installBundle(MavenUtils.toURN(rpom), rpom));
      final List<String> embedded = getEmbeddedDependencies(tasks.iterator()
          .next().bundle);
      if (!"pom".equals(rpom.getPackaging())) {
        tasks.add(installBundle(MavenUtils.toURN(rpom), rpom));
      }
      final List<PomImpl> requiredDependencies = new LinkedList<PomImpl>();
      for (final PomImpl dependency : resolver.getFilteredDependencies(rpom,
          new Filter.CompoundFilter(new Filter.AcceptScopes("compile",
              "runtime"), new Filter.NotAcceptTypes("pom"),
              new Filter.AcceptOptional(false)))) {
        if (!embedded.contains(MavenUtils.toURN(dependency))) {
          requiredDependencies.add(resolver.resolvePom(dependency));
        }
      }
      for (final PomImpl dep : requiredDependencies) {
        tasks.add(installBundle(MavenUtils.toURN(dep), dep));
      }
    } catch (final ParserConfigurationException e) {
      Logger.log(e);
    }
    return tasks;
  }

  /**
   * @param tasks
   * @param update
   * @param refresh
   * @return ...
   */
  public Collection<BundleTask> startOrUpdate(final Set<BundleTask> tasks,
      final boolean update, final boolean refresh) {
    for (final BundleTask task : tasks) {
      try {
        if (task.bundle != null) {
          if (task.installed) {
            start(task);
          } else if (update) {
            update(task);
          }
        }
      } catch (final BundleException e) {
        Logger.log(e);
      } catch (final IOException e) {
        Logger.log(e);
      }
    }

    if (refresh) {
      // Refresh bundles after all updates are done
      refresh(tasks);
    }
    return tasks;
  }

  private void start(final BundleTask task) throws BundleException {
    if (task.bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
      Logger.log("Starting bundle "
          + (task.pom != null ? MavenUtils.toURN(task.pom) : task.bundle
              .getLocation()));
      task.bundle.start();
    }
  }

  private void update(final BundleTask task) throws IOException,
      BundleException {
    InputStream in = null;
    if (task.pom != null) {
      in = new URL(MavenUtils.toUrl(this.repository, task.pom, "jar"))
          .openStream();
    }
    try {
      Logger.log("Updating bundle "
          + (task.pom != null ? MavenUtils.toURN(task.pom) : task.bundle
              .getLocation()));
      task.bundle.update(in);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * @see de.matrixweb.osgi.kernel.maven.Installer#refresh(java.util.Collection)
   */
  @Override
  public void refresh(final Collection<? extends Fragment> fragments) {
    final FrameworkWiring fw = this.framework.adapt(FrameworkWiring.class);
    if (fw != null) {
      final Collection<Bundle> bundles = new ArrayList<Bundle>();
      for (final Fragment fragment : fragments) {
        bundles.add(fragment.getBundle());
      }
      fw.refreshBundles(bundles);
    }
  }

  private BundleTask installBundle(final String location, final PomImpl pom)
      throws IOException, BundleException {
    final BundleTask task = new BundleTask();
    task.pom = pom;
    task.bundle = this.framework.getBundleContext().getBundle(location);
    if (task.bundle == null) {
      Logger.log("Installing bundle " + MavenUtils.toURN(pom));
      final InputStream in = new URL(MavenUtils.toUrl(this.repository, pom,
          "jar")).openStream();
      try {
        task.bundle = this.framework.getBundleContext().installBundle(location,
            in);
        task.installed = true;
      } finally {
        in.close();
      }
    }
    return task;
  }

  private List<String> getEmbeddedDependencies(final Bundle bundle) {
    final List<String> list = new LinkedList<String>();
    final String embeddedArtifacts = bundle.getHeaders().get(
        "Embedded-Artifacts");
    if (embeddedArtifacts != null) {
      final String embedded[] = embeddedArtifacts.split(",");
      for (final String def : embedded) {
        final String[] parts = def.split(";");
        final String groupId = parts[1].substring(3, parts[1].length() - 1);
        final String artifactId = parts[2].substring(3, parts[2].length() - 1);
        final String version = parts[3].substring(3, parts[3].length() - 1);
        list.add("mvn:" + groupId + ':' + artifactId + ':' + version);
      }
    }
    return list;
  }

  /** */
  public static class BundleTask implements Fragment {

    private PomImpl pom;

    private Bundle bundle;

    private boolean installed = false;

    /**
     * @see de.matrixweb.osgi.kernel.maven.Installer.Fragment#getBundle()
     */
    @Override
    public Bundle getBundle() {
      return this.bundle;
    }

    /**
     * @see de.matrixweb.osgi.kernel.maven.Installer.Fragment#getPom()
     */
    @Override
    public Pom getPom() {
      return this.pom;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (this.pom == null ? 0 : this.pom.hashCode());
      return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
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
      final BundleTask other = (BundleTask) obj;
      if (this.pom == null) {
        if (other.pom != null) {
          return false;
        }
      } else if (!this.pom.equals(other.pom)) {
        return false;
      }
      return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return this.pom.toString();
    }

  }

}
