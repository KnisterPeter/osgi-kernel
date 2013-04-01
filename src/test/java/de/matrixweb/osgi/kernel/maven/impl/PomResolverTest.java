package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author markusw
 */
public class PomResolverTest {

  /** */
  @Rule
  public TestName name = new TestName();

  private final PomResolver resolver = new PomResolver(
      "file:src/test/resources/local-m2");

  private void assertListContains(
      final Collection<? extends Artifact> artifacts, final String... urns) {
    final List<String> list = Arrays.asList(urns);
    for (final Artifact artifact : artifacts) {
      assertThat(list.contains(MavenUtils.toURN(artifact)), is(true));
    }
  }

  private void dump(final Pom pom) throws IOException,
      ParserConfigurationException {
    dump(pom, new Filter.AcceptAll());
  }

  private void dump(final Pom pom, final Filter filter) throws IOException,
      ParserConfigurationException {
    System.out.println("\n*****\n" + this.name.getMethodName() + "()\n");
    System.out.println(MavenUtils.dump(this.resolver, filter, pom) + "\n");
  }

  /**
   * Note: This is test-case 'property-resolution'
   * 
   * @throws Exception
   */
  @Test
  public void testPropertyResolution() throws Exception {
    Pom pom = new PomImpl("sko.repro1", "sub1", "1");
    pom = this.resolver.resolvePom(pom);
    assertThat(MavenUtils.toURN(pom), is("mvn:sko.repro1:sub1:1:pom"));
    dump(pom);
    assertThat(pom.getDependencies().size(), is(2));
    assertListContains(pom.getDependencies(), "mvn:sko.repro1:sub2:1:pom",
        "mvn:org.apache.felix:org.osgi.core:1.4.0");
  }

  /**
   * Note: This is test-case 'omit-duplicate'
   * 
   * @throws Exception
   */
  @Test
  public void testNearestDependencyResolutionIncludingExcludes()
      throws Exception {
    Pom pom = new PomImpl("sko.repro", "base", "1");
    pom = this.resolver.resolvePom(pom);
    assertThat(MavenUtils.toURN(pom), is("mvn:sko.repro:base:1"));
    final Filter filter = new Filter.CompoundFilter(new Filter.AcceptScopes(
        "compile", "runtime"), new Filter.NotAcceptTypes("pom"));
    dump(pom, filter);
    final Set<Pom> dependencies = this.resolver.getFilteredDependencies(pom,
        filter);
    assertThat(dependencies.size(), is(2));
    assertListContains(dependencies, "mvn:sko.repro:level1:1",
        "mvn:sko.repro:level2:1");
  }

  /**
   * Note: This is test-case 'managed-dependencies'
   * 
   * @throws Exception
   */
  @Test
  public void testManagedDependencies() throws Exception {
    Pom pom = new PomImpl("group.id", "m2", "1");
    pom = this.resolver.resolvePom(pom);
    assertThat(MavenUtils.toURN(pom), is("mvn:group.id:m2:1:pom"));
    final Filter filter = new Filter.AcceptAll();
    dump(pom, filter);
    final Collection<Pom> dependencies = this.resolver.getFilteredDependencies(
        pom, filter);
    assertThat(dependencies.size(), is(2));
    assertListContains(dependencies, "mvn:group.id:m1:1:pom",
        "mvn:junit:junit:3.8.1");
  }

  /**
   * Note: This is test-case 'dependency-exclusion'
   * 
   * @throws Exception
   */
  @Test
  public void testDependencyExclusion() throws Exception {
    Pom pom = new PomImpl("group.id", "excl-m2", "1");
    pom = this.resolver.resolvePom(pom);
    assertThat(MavenUtils.toURN(pom), is("mvn:group.id:excl-m2:1"));
    final Filter filter = new Filter.AcceptAll();
    dump(pom, filter);
    final Collection<Pom> dependencies = this.resolver.getFilteredDependencies(
        pom, filter);
    assertThat(dependencies.size(), is(2));
    assertListContains(dependencies, "mvn:group.id:excl-m1:1",
        "mvn:junit:junit:4.11");
  }

}
