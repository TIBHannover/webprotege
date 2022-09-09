package edu.stanford.bmir.protege.web.shared.project;

import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 17/10/2013
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsTestCase {

    public static final boolean IN_TRASH = true;

    public static final boolean IS_COMMITTED = true;

    public static final boolean IS_PUSHED= true;

    @Mock
    private ProjectId projectId;

    @Mock
    private UserId owner;

    private long createdAt = 22L;

    @Mock
    private UserId createdBy;

    private long modifiedAt = 33L;

    @Mock
    private UserId modifiedBy;


    private String displayName;

    private String description;

    private ProjectDetails projectDetails;

    private String personalAccessToken;

    @Before
    public void setUp() throws Exception {
        displayName = "DisplayName";
        description = "Description";
        projectDetails = ProjectDetails.get(projectId, displayName, description,
                                            owner,
                                            IN_TRASH,
                                            IS_COMMITTED,
                                            IS_PUSHED,
                                            DictionaryLanguage.rdfsLabel(""),
                                            DisplayNameSettings.empty(),
                                            createdAt,
                                            createdBy,
                                            modifiedAt,
                                            modifiedBy,
                                            personalAccessToken);
    }

    @Test
    public void emptyDisplayNameInConstructorIsOK() {
        assertEquals(projectDetails.getDisplayName(), displayName);
    }

    @Test
    public void emptyDescriptionInConstructorIsOK() {
        assertEquals(projectDetails.getDescription(), description);
    }

    @Test
    public void suppliedProjectIdIsReturnedByAccessor() {
        assertEquals(projectDetails.getProjectId(), projectId);
    }

    @Test
    public void suppliedUserIdIsReturnedByAccessor() {
        assertEquals(projectDetails.getOwner(), owner);
    }

    @Test
    public void suppliedTrashValueIsReturnedByAccessor() {
        assertEquals(projectDetails.isInTrash(), IN_TRASH);
    }

    /**
     * Author Nenad Krdzavac
     * Email nenad.krdzavac@tib.eu
     * Date 24.08.2022.
     */
    @Test
    public void suppliedCommittedValueIsReturnedByAccessor(){assertEquals(projectDetails.isCommitted(), IS_COMMITTED);}

    @Test
    public void suppliedPushedValueIsReturnedByAccessor(){assertEquals(projectDetails.isPushed(), IS_PUSHED);}


    @Test
    public void emptyPersonalAccessTokenInConstructorIsOK() {
        assertEquals(projectDetails.getPersonalAccessToken(), personalAccessToken);
    }

}
