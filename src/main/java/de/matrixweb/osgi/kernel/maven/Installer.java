package de.matrixweb.osgi.kernel.maven;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.osgi.framework.Bundle;

import de.matrixweb.osgi.kernel.maven.Artifact.Pom;

/**
 * @author markusw
 */
public interface Installer {

  /**
   * @param mvnURN
   *          The URN to the maven artifact to install (e.g.
   *          mvn:commons-collections:commons-collections:1.2.3)
   * @return Returns a {@link Collection} of {@link Fragment}s relevant to the
   *         called operation
   * @throws IOException
   */
  Collection<? extends Fragment> installOrUpdate(String mvnURN)
      throws IOException;

  /**
   * @param update
   * @param mvnURN
   *          The URN to the maven artifact to install (e.g.
   *          mvn:commons-collections:commons-collections:1.2.3)
   * @return Returns a {@link Collection} of {@link Fragment}s relevant to the
   *         called operation
   * @throws IOException
   */
  Collection<? extends Fragment> installOrUpdate(boolean update, String mvnURN)
      throws IOException;

  /**
   * @param update
   * @param refresh
   * @param mvnURN
   *          The URN to the maven artifact to install (e.g.
   *          mvn:commons-collections:commons-collections:1.2.3)
   * @return Returns a {@link Collection} of {@link Fragment}s relevant to the
   *         called operation
   * @throws IOException
   */
  Collection<? extends Fragment> installOrUpdate(boolean update,
      boolean refresh, String mvnURN) throws IOException;

  /**
   * @param update
   * @param files
   * @return Returns a {@link Collection} of {@link Fragment}s relevant to the
   *         called operation
   * @throws IOException
   */
  Collection<? extends Fragment> installOrUpdate(boolean update, File... files)
      throws IOException;

  /**
   * @param update
   * @param refresh
   * @param files
   * @return Returns a {@link Collection} of {@link Fragment}s relevant to the
   *         called operation
   * @throws IOException
   */
  Collection<? extends Fragment> installOrUpdate(boolean update,
      boolean refresh, File... files) throws IOException;

  /**
   * @param fragments
   *          The {@link Fragment}s to refresh in the runtime
   */
  void refresh(Collection<? extends Fragment> fragments);

  /**
   * 
   */
  public interface Fragment {

    /**
     * @return Returns the {@link Bundle} associated to this {@link Fragment}
     */
    Bundle getBundle();

    /**
     * @return Returns the {@link Pom} associated to this {@link Fragment}
     */
    Pom getPom();

  }

}
