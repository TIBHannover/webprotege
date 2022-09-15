package edu.stanford.bmir.protege.web.shared.user;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;
import java.io.Serializable;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/06/2012
 */
public class PersonalAccessToken implements Serializable {

    private String token;

    @GwtSerializationConstructor
    private PersonalAccessToken() {
    }

    @JsonCreator
    public PersonalAccessToken(@Nonnull String token) {
        checkNotNull(token);
        this.token = token;
    }

    public boolean isEmpty() {
        return token.isEmpty();
    }

    @Nonnull
    @JsonValue
    public String getPersonalAccessToken() {
        return token;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof PersonalAccessToken)) {
            return false;
        }
        PersonalAccessToken other = (PersonalAccessToken) obj;
        return token.equals(other.token);
    }


    @Override
    public String toString() {
        return toStringHelper("PersonalAccessToken")
                .addValue(token)
                .toString();
    }
}
