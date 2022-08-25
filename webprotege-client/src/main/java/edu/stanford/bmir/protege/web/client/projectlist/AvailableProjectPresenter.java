package edu.stanford.bmir.protege.web.client.projectlist;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import edu.stanford.bmir.protege.web.client.action.AbstractUiAction;
import edu.stanford.bmir.protege.web.client.projectmanager.*;
import edu.stanford.bmir.protege.web.shared.TimeUtil;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.project.AvailableProject.UNKNOWN;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19/02/16
 */
@AutoFactory
public class AvailableProjectPresenter {

    @Nonnull
    private final AvailableProjectView view;

    @Nonnull
    private final AvailableProject project;

    @Nonnull
    private final TrashManagerRequestHandler trashManagerRequestHandler;

    @Nonnull
    private final GithubManagerRequestHandler githubManagerRequestHandler;

    @Nonnull
    private final LoadProjectRequestHandler loadProjectRequestHandler;

    @Nonnull
    private final DownloadProjectRequestHandler downloadProjectRequestHandler;

    @Nonnull
    private LoadProjectInNewWindowRequestHandler loadProjectInNewWindowRequestHandler;

    @Inject
    public AvailableProjectPresenter(@Nonnull AvailableProject project,
                                     @Provided @Nonnull AvailableProjectView view,
                                     @Provided @Nonnull LoadProjectInNewWindowRequestHandler loadProjectInNewWindowRequestHandler,
                                     @Provided @Nonnull TrashManagerRequestHandler trashManagerRequestHandler,
                                     @Provided GithubManagerRequestHandler githubManagerRequestHandler,
                                     @Provided @Nonnull LoadProjectRequestHandler loadProjectRequestHandler,
                                     @Provided @Nonnull DownloadProjectRequestHandler downloadProjectRequestHandler) {
        this.view = checkNotNull(view);
        this.project = checkNotNull(project);
        this.loadProjectInNewWindowRequestHandler = checkNotNull(loadProjectInNewWindowRequestHandler);
        this.trashManagerRequestHandler = checkNotNull(trashManagerRequestHandler);
        this.githubManagerRequestHandler = checkNotNull(githubManagerRequestHandler);
        this.loadProjectRequestHandler = checkNotNull(loadProjectRequestHandler);
        this.downloadProjectRequestHandler = checkNotNull(downloadProjectRequestHandler);
    }

    public void start() {
        view.setProject(project.getProjectId(), project.getDisplayName());
        view.setProjectOwner(project.getOwner());
        String lastOpenedAt;
        if(project.getLastOpenedAt() != UNKNOWN) {
            lastOpenedAt = TimeUtil.getTimeRendering(project.getLastOpenedAt());
        }
        else {
            lastOpenedAt = "";
        }
        view.setLastOpenedAt(lastOpenedAt);

        long modifiedAtTs = project.getLastModifiedAt();
        String modifiedAt;
        if(modifiedAtTs != UNKNOWN) {
            modifiedAt = TimeUtil.getTimeRendering(modifiedAtTs);
        }
        else {
            modifiedAt = "";
        }
        view.setModifiedAt(modifiedAt);
        view.setDescription(project.getDescription());
        view.setInTrash(project.isInTrash());
        view.setLoadProjectRequestHandler(loadProjectRequestHandler);
        addActions();
    }

    public void stop() {
        view.dispose();
    }

    public ProjectId getProjectId() {
        return project.getProjectId();
    }

    public UserId getOwner() {
        return project.getOwner();
    }

    @Nonnull
    public AvailableProjectView getView() {
        return view;
    }

    private void addActions() {
        addOpenAction();
        addOpenInNewWindowAction();
        addDowloadAction();
        addTrashAction();
        addGithubAction();
    }

    /**
     * Author Nenad Krdzavac
     * Email nenad.krdzavac@tib.eu
     * Date 30.06.2022.
     *
     * Adds Github commit or push actions
     */
    private void addGithubAction(){

        String githubActionLabel="Github";

        if(project.canBeCommited()){

            githubActionLabel="Github committed";

        } else {
            if (project.canBePushed()) {

                githubActionLabel = "Github pushed";
            }
        }

        AbstractUiAction githubAction = new AbstractUiAction(githubActionLabel) {

            @Override
            public void execute() {
                /**
                 * If the project is ready can be committed.
                 */
                if (project.canBeCommited()) {

                    //TODO: implement method similar to doDownload()

                     githubManagerRequestHandler.handleCommitProjectToGithub(project.getProjectId());
                }
                else {

                    githubManagerRequestHandler.handlePushProjectToGithub(project.getProjectId());

                }
            }

        };

        githubAction.setEnabled(project.canBeCommited());
        view.addAction(githubAction);

//        view.addAction(new AbstractUiAction("Github") {
//            @Override
//            public void execute() {
//
//            }
//        });
    }
    private void addOpenAction() {
        view.addAction(new AbstractUiAction("Open") {
            @Override
            public void execute() {
                loadProjectRequestHandler.handleProjectLoadRequest(project.getProjectId());
            }
        });
    }

    private void addOpenInNewWindowAction() {
        view.addAction(new AbstractUiAction("Open in new window") {
            @Override
            public void execute() {
                loadProjectInNewWindowRequestHandler.handleLoadProjectInNewWindow(project.getProjectId());
            }
        });
    }

    private void addDowloadAction() {
        AbstractUiAction downloadAction = new AbstractUiAction("Download") {
            @Override
            public void execute() {
                downloadProjectRequestHandler.handleProjectDownloadRequest(project.getProjectId());
            }
        };
        downloadAction.setEnabled(project.isDownloadable());
        view.addAction(downloadAction);
    }

    private void addTrashAction() {
        String trashActionLabel;
        if(project.isInTrash()) {
            trashActionLabel = "Remove from trash";
        }
        else {
            trashActionLabel = "Move to trash";
        }
        AbstractUiAction trashAction = new AbstractUiAction(trashActionLabel) {
            @Override
            public void execute() {
                if (project.isInTrash()) {
                    trashManagerRequestHandler.handleRemoveProjectFromTrash(project.getProjectId());
                }
                else {
                    trashManagerRequestHandler.handleMoveProjectToTrash(project.getProjectId());
                }
            }
        };
        view.addAction(trashAction);
        trashAction.setEnabled(project.isTrashable());
    }


}
