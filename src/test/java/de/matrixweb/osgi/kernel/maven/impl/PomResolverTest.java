package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
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
    final PomImpl pom = new PomImpl("sko.repro1", "sub1", "1");
    new PomResolver("file:src/test/resources/property-resolution/local-m2")
        .resolvePom(pom);

    assertThat(pom.getGroupId(), is("sko.repro1"));
    assertThat(pom.getArtifactId(), is("sub1"));
    assertThat(pom.getVersion(), is("1"));

    assertThat(pom.getDependencies().size(), is(2));
    for (final Dependency dependency : pom.getDependencies()) {
      if ("sko.repro1".equals(dependency.getPom().getGroupId())) {
        assertThat(dependency.getPom().getArtifactId(), is("sub2"));
        assertThat(dependency.getPom().getVersion(), is("1"));
      }
    }

    System.out
        .println("Test PropertyResolution:\n" + new Dumper().dump(1, pom));
  }

  /**
   * @throws Exception
   */
  @Test
  public void testNearestDependencyResolutionIncludingExcludes()
      throws Exception {
    final PomImpl pom = new PomImpl("sko.repro", "base", "1");
    new PomResolver("file:src/test/resources/omit-duplicate/local-m2")
        .resolvePom(pom);
    assertThat(pom.toURN(), is("mvn:sko.repro:base:1"));

    final Set<Dependency> dependencies = pom
        .resolveNearestDependencies(new Filter.CompoundFilter(
            new Filter.AcceptScopes("compile", "runtime"),
            new Filter.NotAcceptTypes("pom")));
    final List<String> list = new ArrayList<String>();
    for (final Dependency dep : dependencies) {
      list.add(dep.getPom().toURN());
    }
    assertThat(list.size(), is(2));
    assertThat(list.contains("mvn:sko.repro:level1:1"), is(true));
    assertThat(list.contains("mvn:sko.repro:level2:1"), is(true));

    System.out.println("Test NearestDependencyResolution:\n"
        + new Dumper().dump(1, pom));
  }

  /**
   * @throws Exception
   */
  @Test
  public void testManagedDependencies() throws Exception {
    final Pom pom = new PomImpl("group.id", "m2", "1");
    new PomResolver("file:src/test/resources/managed-dependencies/local-m2")
        .resolvePom(pom);
    assertThat(pom.toURN(), is("mvn:group.id:m2:1"));

    final Collection<Dependency> dependencies = pom
        .resolveNearestDependencies(new Filter.AcceptAll());
    assertThat(dependencies.size(), is(2));
    final List<String> list = new ArrayList<String>();
    for (final Dependency dep : dependencies) {
      list.add(dep.getPom().toURN());
    }
    assertThat(list.contains("mvn:group.id:m1:1:pom"), is(true));
    assertThat(list.contains("mvn:junit:junit:3.8.1:pom"), is(true));

    System.out.println("Test ManagedDependencies:\n"
        + new Dumper().dump(1, pom));
  }

}
