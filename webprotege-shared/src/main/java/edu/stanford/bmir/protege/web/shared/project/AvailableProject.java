package edu.stanford.bmir.protege.web.shared.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import java.util.Comparator;

import static java.util.Comparator.comparing;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 6 Mar 2017
 *
 * Represents information about a project that is available (viewable) for a given user.
 */
@JsonPropertyOrder({"projectId",
        "displayName",
        "description",
        "owner",
        "createdAt",
        "createdBy",
        "lastModifiedAt",
        "lastModifiedBy",
        "inTrash",
        "isCommitted",
        "isPushed",
        "trashable",
        "downloadable"})
@AutoValue
@GwtCompatible(serializable = true)
public abstract class AvailableProject implements IsSerializable, Comparable<AvailableProject>, HasProjectId {

    public static final long UNKNOWN = 0;

    private static transient Comparator<AvailableProject> comparator = comparing(AvailableProject::getProjectDetails);

    /**
     * Captures the information about a project that is available for the current
     * user.
     *
     * @param projectDetails      The project details.
     * @param downloadable        A flag indicating whether the project is downloadable by the current
     *                            user (in the current session).
     * @param trashable           A flag indicating whether the project can be moved to the trash by
     * @param canBeCommitted      A flag indicating whether the project can be committed and pushed to Github by the current user
     *                            (in the current session)
     * @param lastOpenedTimestamp A time stamp of when the project was last opened by the current
     *                            user.  A zero or negative value indicates unknown.
     */
    public static AvailableProject get(@Nonnull ProjectDetails projectDetails,
                                       boolean downloadable,
                                       boolean trashable,
                                       boolean canBeCommitted,
                                       long lastOpenedTimestamp) {
        return new AutoValue_AvailableProject(projectDetails,
                                              downloadable,
                                              trashable,
                                              canBeCommitted,
                                              lastOpenedTimestamp);
    }


    /**
     * Gets the {@link ProjectId}
     *
     * @return The {@link ProjectId}
     */
    @Override
    @Nonnull
    public ProjectId getProjectId() {
        return getProjectDetails().getProjectId();
    }


    /**
     * Gets the display name of the project.
     *
     * @return A string representing the display name.
     */
    @Nonnull
    public String getDisplayName() {
        return getProjectDetails().getDisplayName();
    }


    /**
     * Gets the owner of the project.
     *
     * @return The owner of the project represented by a {@link UserId}.
     */
    @Nonnull
    public UserId getOwner() {
        return getProjectDetails().getOwner();
    }

    /**
     * Gets the project description.
     *
     * @return A possibly empty string representing the project description.
     */
    @Nonnull
    public String getDescription() {
        return getProjectDetails().getDescription();
    }

    @Nonnull
    public String getRepoURI() {
        return getProjectDetails().getRepoURI();
    }

    /**
     * Determines whether this project is in the trash or not.
     *
     * @return true if the project is in the trash, otherwise false.
     */
    public boolean isInTrash() {
        return getProjectDetails().isInTrash();
    }

    /**
     * Author Nenad Krdzavac
     * Email nenad.krdzavac@tib.eu
     *
     * Determines whether this project is committed to Github or not.
     *
     * @return true is the project is committed to Github, otherwise false.
     */
    public boolean isCommitted(){return getProjectDetails().isCommitted();}

    /**
     * Author Nenad Krdzavac
     * Email nenad.krdzavac@tib.eu
     *
     * Determines whether this pushed to Github or not
     *
     * @return true if project is pushed to Github, otherwise false.
     */
    public boolean isPushed(){return getProjectDetails().isPushed();}

    /**
     * Gets the timestamp of when the project was created.
     *
     * @return A long representing the timestamp.
     */
    public long getCreatedAt() {
        return getProjectDetails().getCreatedAt();
    }

    /**
     * Get the user who created the project.
     *
     * @return A {@link UserId} representing the user who created the project.
     */
    @Nonnull
    public UserId getCreatedBy() {
        return getProjectDetails().getCreatedBy();
    }

    /**
     * Gets the timestamp of when the project was last modified.
     *
     * @return A long representing a timestamp.
     */
    public long getLastModifiedAt() {
        return getProjectDetails().getLastModifiedAt();
    }

    /**
     * Get the id of the user who last modified the project.
     *
     * @return A {@link UserId} identifying the user who last modified the project.
     */
    @Nonnull
    public UserId getLastModifiedBy() {
        return getProjectDetails().getLastModifiedBy();
    }

    /**
     * Gets all of the project details as a ProjectDetails object.
     *
     * @return The details as a {@link ProjectDetails} object.
     */
    @Nonnull
    @JsonIgnore
    public abstract ProjectDetails getProjectDetails();

    /**
     * Determines if this project is downloadable (by the current user).
     *
     * @return true if the project is downloadable, otherwise false.
     */
    public abstract boolean isDownloadable();


    /**
     * Determines if this project is trashable (by the current user).
     *
     * @return true if the project is trashable, otherwise false.
     */
    public abstract boolean isTrashable();

    /**
     * Author Nenad Krdzavac
     * Email nenad.krdzavac@tib.eu
     *
     * Determines if this project can be committed (by the current user) to Github.
     *
     * @return true is the project can be committed, otherwise false.
     */
    public abstract boolean canBeCommited();

    /**
     * Gets the timestamp of when the project was last opened by the current user.
     *
     * @return The timestamp.  A value of 0 or a negative value indicated unknown.
     */
    public abstract long getLastOpenedAt();

    @Override
    public int compareTo(AvailableProject o) {
        return comparator.compare(this, o);
    }
}
