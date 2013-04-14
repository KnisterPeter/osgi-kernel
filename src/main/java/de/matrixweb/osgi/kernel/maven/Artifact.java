package de.matrixweb.osgi.kernel.maven;

import java.util.Collection;

import de.matrixweb.osgi.kernel.maven.impl.PomImpl;

/**
 * @author markusw
 */
public interface Artifact {

  /**
   * @return the groupId
   */
  String getGroupId();

  /**
   * @return the artifactId
   */
  String getArtifactId();

  /**
   * @return the version
   */
  String getVersion();

  /**
   * @return Return packaging or type
   */
  String getPackagingOrType();

  /**
   * @return Returns a URN for this {@link Artifact}
   */
  String toURN();

  /**
   * 
   */
  public interface Pom extends Artifact {

    /**
     * @return Returns the {@link Dependency}s of this {@link PomImpl}
     */
    Collection<? extends Dependency> getDependencies();

  }

  /**
   * 
   */
  public interface Dependency extends Artifact {

    /**
     * @return Returns the scope of this {@link Dependency}
     */
    String getScope();

    /**
     * @return Returns the type of this {@link Dependency}
     */
    String getType();

    /**
     * @return Returns true if this {@link Dependency} is optional, false
     *         otherwise
     */
    boolean isOptional();

  }

}
