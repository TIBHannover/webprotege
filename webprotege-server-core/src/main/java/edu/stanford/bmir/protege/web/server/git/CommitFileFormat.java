package edu.stanford.bmir.protege.web.server.git;

import edu.stanford.bmir.protege.web.shared.git.CommitFormatExtension;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import java.util.function.Supplier;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/07/2013
 */
public enum CommitFileFormat {

    RDF_XML(RDFXMLDocumentFormat::new, "application/rdf+xml", CommitFormatExtension.owl),

    RDF_TURLE(TurtleDocumentFormat::new, "text/turtle", CommitFormatExtension.ttl),

    OWL_XML(OWLXMLDocumentFormat::new, "application/owl+xml", CommitFormatExtension.owx),

    MANCHESTER(ManchesterSyntaxDocumentFormat::new, "text/owl-manchester", CommitFormatExtension.omn),

    FUNCTIONAL_SYNTAX(FunctionalSyntaxDocumentFormat::new, "text/owl-functional", CommitFormatExtension.ofn);


    private final Supplier<OWLDocumentFormat> documentFormatSupplier;

    private final String mimeType;

    private final CommitFormatExtension extension;

    CommitFileFormat(Supplier<OWLDocumentFormat> documentFormatSupplier, String mimeType, CommitFormatExtension extension) {
        this.documentFormatSupplier = documentFormatSupplier;
        this.mimeType = mimeType;
        this.extension = extension;
    }


    public String getParameterValue() {
        return extension.getExtension();
    }

    public OWLDocumentFormat getDocumentFormat() {
        return documentFormatSupplier.get();
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension.getExtension();
    }


    /**
     * Gets the format for the specified name.
     * @param parameterName The parameter name.  May be {@code null}.
     * @return The format that has the specified parameter name, or the default format (returned by
     * {@link #getDefaultFormat()} if the paramter name is {@code null} or the parameter name is not recognised).
     * Not {@code null}.
     */
    public static CommitFileFormat getFileFormatFromParameterName(String parameterName) {
        if(parameterName == null) {
            return getDefaultFormat();
        }
        for(CommitFileFormat format : values()) {
            if(format.getParameterValue().equals(parameterName)) {
                return format;
            }
        }
        return getDefaultFormat();
    }

    /**
     * Gets the default format.
     * @return The default format.  Not {@code null}.
     */
    public static CommitFileFormat getDefaultFormat() {
        return RDF_XML;
    }
}
