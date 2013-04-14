package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.Matchers.*;
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

import de.matrixweb.osgi.kernel.maven.Artifact;

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
      assertThat(artifact.toURN(), isIn(list));
    }
  }

  private void expectDependencies(final Collection<? extends Artifact> actual,
      final String... expected) {
    assertThat(actual.size(), is(expected.length));
    assertListContains(actual, expected);
  }

  private void dump(final PomImpl pom) throws IOException,
      ParserConfigurationException {
    dump(pom, new Filter.AcceptAll());
  }

  private void dump(final PomImpl pom, final Filter filter) throws IOException,
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
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("sko.repro1",
        "sub1", "1"));
    assertThat(pom.toURN(), is("mvn:sko.repro1:sub1:1:pom"));
    dump(pom);
    expectDependencies(pom.getDependencies(), "mvn:sko.repro1:sub2:1:pom");
  }

  /**
   * Note: This is test-case 'omit-duplicate'
   * 
   * @throws Exception
   */
  @Test
  public void testNearestDependencyResolutionIncludingExcludes()
      throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("sko.repro",
        "base", "1"));
    assertThat(pom.toURN(), is("mvn:sko.repro:base:1"));
    final Filter filter = new Filter.CompoundFilter(new Filter.AcceptScopes(
        "compile", "runtime"), new Filter.NotAcceptTypes("pom"));
    dump(pom, filter);
    final Set<PomImpl> dependencies = this.resolver.getFilteredDependencies(
        pom, filter);
    expectDependencies(dependencies, "mvn:sko.repro:level1:1",
        "mvn:sko.repro:level2:1");
  }

  /**
   * Note: This is test-case 'managed-dependencies'
   * 
   * @throws Exception
   */
  @Test
  public void testManagedDependencies() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("group.id", "m2",
        "1"));
    assertThat(pom.toURN(), is("mvn:group.id:m2:1:pom"));
    final Filter filter = new Filter.AcceptAll();
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    expectDependencies(dependencies, "mvn:group.id:m1:1:pom",
        "mvn:junit:junit:3.8.1");
  }

  /**
   * Note: This is test-case 'dependency-exclusion'
   * 
   * @throws Exception
   */
  @Test
  public void testDependencyExclusion() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("group.id",
        "excl-m2", "1"));
    assertThat(pom.toURN(), is("mvn:group.id:excl-m2:1"));
    final Filter filter = new Filter.AcceptAll();
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    expectDependencies(dependencies, "mvn:group.id:excl-m1:1",
        "mvn:junit:junit:4.11");
  }

  /**
   * Note: This is test-case 'dependency-exclusion2'
   * 
   * @throws Exception
   */
  @Test
  public void testDependencyExclusion2() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("sko.repro4",
        "base", "1"));
    assertThat(pom.toURN(), is("mvn:sko.repro4:base:1"));
    final Filter filter = new Filter.AcceptAll();
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    expectDependencies(dependencies, "mvn:sko.repro4:dep1:1",
        "mvn:sko.repro4:dep2:1", "mvn:sko.repro4:lib:1");
  }

  /**
   * Note: This is test-case 'optional-dependencies'
   * 
   * @throws Exception
   */
  @Test
  public void testOptionalDependencies() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("group.id",
        "optional-dependencies", "1"));
    assertThat(pom.toURN(), is("mvn:group.id:optional-dependencies:1"));
    final Filter filter = new Filter.AcceptOptional(false);
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    assertThat(dependencies.size(), is(0));
  }

  /**
   * Note: This is test-case 'optional-dependencies2'
   * 
   * @throws Exception
   */
  @Test
  public void testOptionalDependencies2() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("sko.repro3",
        "base", "1"));
    assertThat(pom.toURN(), is("mvn:sko.repro3:base:1"));
    final Filter filter = new Filter.AcceptOptional(false);
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    expectDependencies(dependencies, "mvn:sko.repro3:dep1:1",
        "mvn:sko.repro3:dep2:1", "mvn:sko.repro3:lib:2");
  }

  /**
   * Note: This is test-case 'transitive-exclusion'
   * 
   * @throws Exception
   */
  @Test
  public void testTransitiveExclusion() throws Exception {
    final PomImpl pom = this.resolver.resolvePom(new PomImpl("sko.repro6",
        "base", "1"));
    assertThat(pom.toURN(), is("mvn:sko.repro6:base:1"));
    final Filter filter = new Filter.AcceptOptional(false);
    dump(pom, filter);
    final Collection<PomImpl> dependencies = this.resolver
        .getFilteredDependencies(pom, filter);
    assertThat(dependencies.size(), is(2));
    assertListContains(dependencies, "mvn:sko.repro6:level1:1",
        "mvn:sko.repro6:level2:1");
  }

}
