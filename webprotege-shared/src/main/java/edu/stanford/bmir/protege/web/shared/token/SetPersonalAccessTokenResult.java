package edu.stanford.bmir.protege.web.shared.token;

import com.google.common.base.Objects;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class SetPersonalAccessTokenResult implements Result {

    public enum Result {
        /**
         * The address was changed.
         */
        TOKEN_CHANGED,
        /**
         * The user and the address are the same, therefore the address has not been changed.
         */
        TOKEN_UNCHANGED,
        /**
         * A different user with the specified address exists and the address was not changed.
         */
        TOKEN_ALREADY_EXISTS
    }

    private Result result;

    /**
     * For serialization only
     */
    private SetPersonalAccessTokenResult() {
    }

    public SetPersonalAccessTokenResult(Result result) {
        this.result = checkNotNull(result);
    }

    public Result getResult() {
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SetPersonalAccessTokenResult)) {
            return false;
        }
        SetPersonalAccessTokenResult other = (SetPersonalAccessTokenResult) obj;
        return this.result == other.result;
    }

    @Override
    public String toString() {
        return toStringHelper("SetPersonalAccessTokenResult")
                .addValue(result)
                .toString();
    }
}
