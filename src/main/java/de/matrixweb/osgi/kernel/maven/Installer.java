package de.matrixweb.osgi.kernel.maven;

import java.io.File;
import java.io.IOException;

/**
 * @author markusw
 */
public interface Installer {

  /**
   * @param mvnURN
   * @throws IOException
   */
  void installOrUpdate(String mvnURN) throws IOException;

  /**
   * @param update
   * @param mvnURN
   * @throws IOException
   */
  void installOrUpdate(boolean update, String mvnURN) throws IOException;

  /**
   * @param update
   * @param file
   * @throws IOException
   */
  void installOrUpdate(boolean update, File... file) throws IOException;

}
