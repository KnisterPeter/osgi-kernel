package de.matrixweb.osgi.kernel.maven.impl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author markusw
 */
public class PomParser extends DefaultHandler {

  private final Pom pom;

  private Dependency dependency = new DependencyImpl(new PomImpl());

  private final Artifact exclusion = new ArtifactImpl();

  private final StringBuilder content = new StringBuilder();
  private boolean inParent = false;
  private boolean inProperties = false;
  private boolean inDependencyManagement = false;
  private boolean inDependencies = false;
  private boolean inDependency = false;
  private boolean inExclusions = false;
  private boolean inExclusion = false;
  private boolean inBuild = false;
  private boolean inProfiles = false;

  /**
   * @param pom
   */
  public PomParser(final Pom pom) {
    super();
    this.pom = pom;
  }

  @Override
  public void startElement(final String uri, final String localName,
      final String qName, final Attributes attributes) throws SAXException {
    this.content.setLength(0);
    if ("parent".equals(qName)) {
      this.inParent = true;
    } else if ("build".equals(qName)) {
      this.inBuild = true;
    } else if ("profiles".equals(qName)) {
      this.inProfiles = true;
    } else if ("properties".equals(qName)) {
      this.inProperties = true;
    } else if ("dependencyManagement".equals(qName)) {
      this.inDependencyManagement = true;
    } else if ("dependencies".equals(qName)) {
      this.inDependencies = true;
    } else if ("dependency".equals(qName)) {
      this.inDependency = true;
    } else if ("exclusions".equals(qName)) {
      this.inExclusions = true;
    } else if ("exclusion".equals(qName)) {
      this.inExclusion = true;
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(final char[] ch, final int start, final int length)
      throws SAXException {
    this.content.append(ch, start, length);
  }

  @Override
  public void endElement(final String uri, final String localName,
      final String qName) throws SAXException {
    if (this.inParent) {
      endElementInParent(qName);
    } else if (this.inBuild) {
      if ("build".equals(qName)) {
        this.inBuild = false;
      }
    } else if (this.inProfiles) {
      if ("profiles".equals(qName)) {
        this.inProfiles = false;
      }
    } else if (this.inDependencyManagement) {
      endElementInDependencyManagement(qName, new DependencyCallback() {
        @Override
        public void addDependency(final Dependency dependency) {
          PomParser.this.pom.addManagedDependency(dependency);
        }
      });
    } else if (this.inDependencies) {
      endElementInDependencies(qName, new DependencyCallback() {
        @Override
        public void addDependency(final Dependency dependency) {
          PomParser.this.pom.addDependency(dependency);
        }
      });
    } else if (this.inProperties) {
      if ("properties".equals(qName)) {
        this.inProperties = false;
      } else {
        this.pom.addProperty(qName, this.content.toString());
      }
    } else if ("packaging".equals(qName)) {
      this.pom.setPackaging(this.content.toString());
    }
  }

  /**
   * TODO: Do not use the dependency holder for parsing the parent pom elements
   */
  private void endElementInParent(final String qName) {
    if ("parent".equals(qName)) {
      this.inParent = false;
      this.pom.setParent(new PomImpl(this.pom, this.dependency.getPom()));
      this.dependency = new DependencyImpl(new PomImpl());
    } else if ("groupId".equals(qName)) {
      this.dependency.getPom().setGroupId(this.content.toString());
    } else if ("artifactId".equals(qName)) {
      this.dependency.getPom().setArtifactId(this.content.toString());
    } else if ("version".equals(qName)) {
      this.dependency.getPom().setVersion(this.content.toString());
    }
  }

  private void endElementInDependencyManagement(final String qName,
      final DependencyCallback callback) {
    if ("dependencyManagement".equals(qName)) {
      this.inDependencyManagement = false;
    } else if (this.inDependencies) {
      endElementInDependencies(qName, callback);
    }
  }

  private void endElementInDependencies(final String qName,
      final DependencyCallback callback) {
    if ("dependencies".equals(qName)) {
      this.inDependencies = false;
    } else if (this.inDependency) {
      if ("dependency".equals(qName)) {
        this.inDependency = false;
        callback.addDependency(new DependencyImpl(new PomImpl(this.pom,
            this.dependency.getPom())));
        this.dependency = new DependencyImpl(new PomImpl());
      } else if (this.inExclusions) {
        endElementInExclusions(qName);
      } else if ("groupId".equals(qName)) {
        this.dependency.getPom().setGroupId(this.content.toString());
      } else if ("artifactId".equals(qName)) {
        this.dependency.getPom().setArtifactId(this.content.toString());
      } else if ("version".equals(qName)) {
        this.dependency.getPom().setVersion(this.content.toString());
      } else if ("type".equals(qName)) {
        this.dependency.setType(this.content.toString());
      } else if ("scope".equals(qName)) {
        this.dependency.setScope(this.content.toString());
      } else if ("optional".equals(qName)) {
        this.dependency.setOptional(Boolean.parseBoolean(this.content
            .toString()));
      }
    }
  }

  private void endElementInExclusions(final String qName) {
    if ("exclusions".equals(qName)) {
      this.inExclusions = false;
    } else if (this.inExclusion) {
      if ("exclusion".equals(qName)) {
        this.inExclusion = false;
        this.dependency.getPom().addExclusion(
            this.exclusion.getGroupId() + ':' + this.exclusion.getArtifactId());
        this.exclusion.clear();
      } else if ("groupId".equals(qName)) {
        this.exclusion.setGroupId(this.content.toString());
      } else if ("artifactId".equals(qName)) {
        this.exclusion.setArtifactId(this.content.toString());
      }
    }
  }

  private interface DependencyCallback {

    void addDependency(Dependency dependency);

  }

}
