package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    final Pom pom = new Pom("sko.repro1", "sub1", "1");
    new PomResolver("file:src/test/resources/property-resolution/local-m2")
        .resolvePom(pom);

    assertThat(pom.getGroupId(), is("sko.repro1"));
    assertThat(pom.getArtifactId(), is("sub1"));
    assertThat(pom.getVersion(), is("1"));

    assertThat(pom.getDependencies().size(), is(2));
    for (final Pom dependency : pom.getDependencies()) {
      if ("sko.repro1".equals(dependency.getGroupId())) {
        assertThat(dependency.getArtifactId(), is("sub2"));
        assertThat(dependency.getVersion(), is("1"));
      }
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void testNearestDependencyResolutionIncludingExcludes()
      throws Exception {
    final Pom pom = new Pom("sko.repro", "base", "1");
    new PomResolver("file:src/test/resources/omit-duplicate/local-m2")
        .resolvePom(pom);
    final Set<Pom> dependencies = pom
        .resolveNearestDependencies(new Filter.CompoundFilter(
            new Filter.AcceptScopes("compile", "runtime"),
            new Filter.NotAcceptTypes("pom")));
    final List<String> list = new ArrayList<String>();
    for (final Pom dep : dependencies) {
      list.add(dep.toURN());
    }
    assertThat(list.size(), is(3));
    assertThat(list.contains("mvn:sko.repro:base:1"), is(true));
    assertThat(list.contains("mvn:sko.repro:level1:1"), is(true));
    assertThat(list.contains("mvn:sko.repro:level2:1"), is(true));
  }

}
