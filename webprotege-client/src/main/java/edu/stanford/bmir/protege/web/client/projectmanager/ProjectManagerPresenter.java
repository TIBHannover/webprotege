package edu.stanford.bmir.protege.web.client.projectmanager;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import edu.stanford.bmir.protege.web.client.app.Presenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.events.UserLoggedInEvent;
import edu.stanford.bmir.protege.web.client.events.UserLoggedOutEvent;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserManager;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserPresenter;
import edu.stanford.bmir.protege.web.shared.event.ProjectMovedFromTrashEvent;
import edu.stanford.bmir.protege.web.shared.event.ProjectMovedToTrashEvent;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.project.GetAvailableProjectsAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

import static edu.stanford.bmir.protege.web.client.events.UserLoggedInEvent.ON_USER_LOGGED_IN;
import static edu.stanford.bmir.protege.web.client.events.UserLoggedOutEvent.ON_USER_LOGGED_OUT;
import static edu.stanford.bmir.protege.web.client.projectmanager.ProjectCreatedEvent.ON_PROJECT_CREATED;
import static edu.stanford.bmir.protege.web.client.projectmanager.ProjectManagerViewFilter.*;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.CREATE_EMPTY_PROJECT;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.CREATE_EMPTY_PROJECT_FROM_GIT;
import static edu.stanford.bmir.protege.web.shared.event.ProjectMovedFromTrashEvent.ON_PROJECT_MOVED_FROM_TRASH;
import static edu.stanford.bmir.protege.web.shared.event.ProjectMovedToTrashEvent.ON_PROJECT_MOVED_TO_TRASH;
import static edu.stanford.bmir.protege.web.shared.project.AvailableProject.UNKNOWN;
import static edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettingsChangedEvent.ON_PROJECT_SETTINGS_CHANGED;
import static java.util.Arrays.asList;
import static java.util.Comparator.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 09/04/2013
 */
@ApplicationSingleton
public class ProjectManagerPresenter implements Presenter {

    private final DispatchServiceManager dispatchServiceManager;

    private final LoggedInUserManager loggedInUserManager;

    private final ProjectManagerView projectManagerView;

    private final LoggedInUserPresenter loggedInUserPresenter;

    private final CreateProjectRequestHandler createProjectRequestHandler;

    private final CreateGithubProjectRequestHandler createGithubProjectRequestHandler ;

    private final Map<ProjectManagerViewFilter, AvailableProjectFilter> viewCat2Filter = new HashMap<>();

    private AvailableProjectFilter currentFilter = new AvailableProjectOrFilter(Collections.emptyList());

    private AvailableProjectsCache availableProjectsCache = new AvailableProjectsCache();

    private Comparator<AvailableProject> sortingComparator = getLastOpenedSortingComparator();

    @Inject
    public ProjectManagerPresenter(@Nonnull ProjectManagerView projectManagerView,
                                   @Nonnull DispatchServiceManager dispatchServiceManager,
                                   @Nonnull LoggedInUserManager loggedInUserManager,
                                   @Nonnull LoggedInUserPresenter loggedInUserPresenter,
                                   @Nonnull CreateProjectRequestHandler createProjectRequestHandler,
                                   /**
                                        * Author Nenad Krdzavac
                                        * Email nenad.krdzavac@tib.eu
                                        *
                                        * Github project request handler
                                        */
                                   @Nonnull CreateGithubProjectRequestHandler createGithubProjectRequestHandler) {
        this.projectManagerView = projectManagerView;
        this.dispatchServiceManager = dispatchServiceManager;
        this.loggedInUserManager = loggedInUserManager;
        this.loggedInUserPresenter = loggedInUserPresenter;
        this.createProjectRequestHandler = createProjectRequestHandler;
        /**
         * Author Nenad Krdzavac
         * Email nenad.krdzavac@tib.eu
         *
         * Github project request handler
         */
        this.createGithubProjectRequestHandler = createGithubProjectRequestHandler;

        viewCat2Filter.put(OWNED_BY_ME,
                           p -> !p.isInTrash() && p.getOwner().equals(loggedInUserManager.getLoggedInUserId()));

        viewCat2Filter.put(SHARED_WITH_ME,
                           p -> !p.isInTrash() && !p.getOwner().equals(loggedInUserManager.getLoggedInUserId()));

        viewCat2Filter.put(TRASH,
                           p -> p.isInTrash() && p.getOwner().equals(loggedInUserManager.getLoggedInUserId()));

        projectManagerView.setViewFilterChangedHandler(this::applyFilters);
        projectManagerView.setViewFilters(asList(OWNED_BY_ME, SHARED_WITH_ME));
        projectManagerView.setSortByKeyChangeHandler(this::handleSortByKeyChanged);
    }

    public void start(@Nonnull AcceptsOneWidget container,
                      @Nonnull EventBus eventBus) {
        GWT.log("[ProjectManagerPresenter] Starting presenter");
        eventBus.addHandler(ON_PROJECT_CREATED, this::handleProjectCreated);
        eventBus.addHandler(ON_USER_LOGGED_IN, this::handleUserLoggedLoggedIn);
        eventBus.addHandler(ON_USER_LOGGED_OUT, this::handleUserLoggedLoggedOut);
        eventBus.addHandler(ON_PROJECT_MOVED_TO_TRASH, this::handleProjectMovedToTrash);
        eventBus.addHandler(ON_PROJECT_MOVED_FROM_TRASH, this::handleProjectMovedFromTrash);
        eventBus.addHandler(ON_PROJECT_SETTINGS_CHANGED, event -> reloadFromServer());

        projectManagerView.setCreateProjectRequestHandler(createProjectRequestHandler);

        /**
         * Author Nenad Krdzavac
         * Email  nenad.krdzavac@tib.eu
         *
         * set Github project request handler.
         */
        projectManagerView.setCreateGithubProjectRequestHandler(createGithubProjectRequestHandler);

        loggedInUserPresenter.start(projectManagerView.getLoggedInUserButton(), eventBus);
        container.setWidget(projectManagerView);
        updateView();
    }

    private void handleProjectMovedFromTrash(ProjectMovedFromTrashEvent event) {
        if (availableProjectsCache.setInTrash(event.getProjectId(), false)) {
            reloadFromClientCache();
        }
    }

    private void handleProjectMovedToTrash(ProjectMovedToTrashEvent event) {
        if(availableProjectsCache.setInTrash(event.getProjectId(), true)) {
            reloadFromClientCache();
        }
    }

    private void handleProjectCreated(ProjectCreatedEvent event) {
        /**
         * Author Nenad Krdzavac
         * Email nenad.krdzavac@tib.eu
         *
         * Assigned true to canBeCommitted and assign false to canBePushed
         */
        AvailableProject availableProject = AvailableProject.get(event.getProjectDetails(), true, true, true, true,UNKNOWN);
        insertAndSelectAvailableProject(availableProject, event.getProjectId());
    }

    private void handleUserLoggedLoggedIn(UserLoggedInEvent event) {
        updateView();
    }

    private void handleUserLoggedLoggedOut(UserLoggedOutEvent event) {
        updateView();
    }


    private void handleSortByKeyChanged(ChangeEvent event) {
        String sortByKey = projectManagerView.getSortByKey();
        AvailableProjectSortingKey key = AvailableProjectSortingKey.valueOf(sortByKey);
        switch (key) {
            case PROJECT_NAME:
                sortingComparator = getProjectNameComparator();
                break;
            case LAST_OPENED:
                sortingComparator = getLastOpenedSortingComparator();
                break;
            case LAST_MODIFIED:
                sortingComparator = getLastModifiedComparator();
                break;
            case OWNER:
                sortingComparator = getOwnerComparator();
                break;
        }
        reloadFromClientCache();
    }

    private static Comparator<AvailableProject> getOwnerComparator() {
        return comparing(AvailableProject::getOwner).thenComparing(naturalOrder());
    }

    private static Comparator<AvailableProject> getLastModifiedComparator() {
        return comparing(AvailableProject::getLastModifiedAt, reverseOrder()).thenComparing(naturalOrder());
    }

    private static Comparator<AvailableProject> getProjectNameComparator() {
        return comparing(AvailableProject::getDisplayName).thenComparing(naturalOrder());
    }

    private static Comparator<AvailableProject> getLastOpenedSortingComparator() {
        return comparing(AvailableProject::getLastOpenedAt, reverseOrder()).thenComparing(naturalOrder());
    }

    private void insertAndSelectAvailableProject(AvailableProject availableProject, ProjectId projectId) {
        availableProjectsCache.add(availableProject);
        projectManagerView.addAvailableProject(availableProject);
        projectManagerView.setSelectedProject(projectId);
    }

    private void applyFilters() {
        List<ProjectManagerViewFilter> selectedFilters = projectManagerView.getViewFilters();
        List<AvailableProjectFilter> filterList = new ArrayList<>();
        for(ProjectManagerViewFilter filter : selectedFilters) {
            AvailableProjectFilter detailsFilter = viewCat2Filter.get(filter);
            filterList.add(detailsFilter);
        }
        currentFilter = new AvailableProjectOrFilter(filterList);
        reloadFromClientCache();
    }

    public ProjectManagerView getView() {
        return projectManagerView;
    }

    private void reloadFromClientCache() {
        displayProjectDetails();
    }

    private void reloadFromServer() {
        reloadFromServer(Optional.empty());
    }

    private void reloadFromServer(Optional<ProjectId> selectId) {
        GetAvailableProjectsAction action = new GetAvailableProjectsAction();
        dispatchServiceManager.execute(action, result -> {
            availableProjectsCache.setAvailableProjects(result.getDetails());
            applyFilters();
            selectId.ifPresent(projectManagerView::setSelectedProject);
        });
    }

    private void displayProjectDetails() {
        List<AvailableProject> entries = Lists.newArrayList();
        List<AvailableProject> availableProjectsList = availableProjectsCache.getAvailableProjectsList();
        availableProjectsList.sort(sortingComparator);
        for(AvailableProject pd : availableProjectsList) {
            if (currentFilter.isIncluded(pd)) {
                entries.add(pd);
            }
        }
        projectManagerView.setAvailableProjects(entries);
    }

    private void updateView() {

        projectManagerView.setCreateProjectEnabled(loggedInUserManager.isAllowedApplicationAction(CREATE_EMPTY_PROJECT));
        /**
         * Author Nenad Krdzavac
         * Email nenad.krdzavac@tib.eu
         */
        projectManagerView.setCreateGitRepoProjectEnabled(true);

        reloadFromServer();
    }
}