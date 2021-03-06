package de.matrixweb.osgi.kernel.maven.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

/**
 * @author markusw
 */
public class PomParserTest {

  /**
   * @throws Exception
   */
  @Test
  public void test() throws Exception {
    final PomImpl pom = new PomImpl("groupId", "artifactId", "version");
    final InputStream is = new FileInputStream(new File("pom.xml"));
    SAXParserFactory.newInstance().newSAXParser().parse(is, new PomParser(pom));
    assertThat(pom.getPackaging(), is("jar"));
    assertThat(pom.getDependencies().size(), is(greaterThan(0)));
  }

}
