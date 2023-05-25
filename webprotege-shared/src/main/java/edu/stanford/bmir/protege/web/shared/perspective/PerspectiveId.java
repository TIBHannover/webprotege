package edu.stanford.bmir.protege.web.shared.perspective;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.util.UUIDUtil;
import jdk.internal.org.jline.utils.Log;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;
import com.allen_sauer.gwt.log.client.Log;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12/02/16
 */
@AutoValue
@GwtCompatible(serializable = true)
public abstract class PerspectiveId implements IsSerializable, Serializable {

    @JsonCreator
    @Nonnull
    public static PerspectiveId get(@Nonnull String id) {
        Log.debug("PerspectiveId: " + id);
        if(!UUIDUtil.isWellFormed(id)) {
            Log.debug("Malformed PerspectiveId.  PerspectiveIds must be UUIDs: " + id);
            throw new IllegalArgumentException("Malformed PerspectiveId.  PerspectiveIds must be UUIDs");
        }
        return new AutoValue_PerspectiveId(id);
    }

    @GwtIncompatible
    public static PerspectiveId generate() {
        return get(UUID.randomUUID().toString());
    }

    /**
     * Gets the identifier for this perspective.  This is a human readable name.
     */
    @JsonValue
    @Nonnull
    public abstract String getId();
}
