package edu.stanford.bmir.protege.web.shared.github;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 02.09.2022
 *
 * This class represents format extension of ontology files that we commit, push and pull from Github.
 */
public enum GithubFormatExtension {

    owl("RDF/XML"),

    ttl("Turtle"),

    owx("OWL/XML"),

    omn("Manchester OWL Syntax"),

    ofn("Functional OWL Syntax");

    private String displayName;

    GithubFormatExtension(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExtension() {
        return name();
    }
}
