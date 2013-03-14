package de.matrixweb.osgi.kernel.utils;

/**
 * @author marwol
 */
public final class Logger {

  private Logger() {
  }

  /**
   * @param t
   *          {@link Throwable} to log
   */
  public static void log(final Throwable t) {
    t.printStackTrace();
  }

}
