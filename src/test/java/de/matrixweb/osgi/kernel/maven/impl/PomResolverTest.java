package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

/**
 * @author markusw
 */
public class PomResolverTest {

  /**
   * @throws Exception
   */
  @Test
  public void testPropertyResolution() throws Exception {
    final Pom pom = new Pom("sko.repo1", "sub1", "1");
    final InputStream is = getClass().getResourceAsStream(
        "/property-resolution/sub1/pom.xml");
    try {
      new PomResolver("file:/target/.m2").resolvePom(pom, is);

      assertThat(pom.getGroupId(), is("sko.repo1"));
      assertThat(pom.getArtifactId(), is("sub1"));
      assertThat(pom.getVersion(), is("1"));

      assertThat(pom.getDependencies().size(), is(2));
      for (final Pom dependency : pom.getDependencies()) {
        if ("sko.repo1".equals(dependency.getGroupId())) {
          assertThat(dependency.getArtifactId(), is("sub2"));
          assertThat(dependency.getVersion(), is("1"));
        }
      }

    } finally {
      is.close();
    }
  }

}
