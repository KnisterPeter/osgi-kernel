package de.matrixweb.osgi.kernel.utils;

/**
 * @author marwol
 */
public final class Logger {

  private Logger() {
  }

  /**
   * @param message
   *          The message to print
   */
  public static void log(final String message) {
    System.out.println(message);
  }

  /**
   * @param t
   *          {@link Throwable} to log
   */
  public static void log(final Throwable t) {
    t.printStackTrace();
  }

}
