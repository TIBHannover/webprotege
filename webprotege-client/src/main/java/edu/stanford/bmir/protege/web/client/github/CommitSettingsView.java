package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 31.08.2022
 */
public interface CommitSettingsView extends IsWidget, HasInitialFocusable {

    GithubFormatExtension getGithubFormatExtension();

    void setGithubFormatExtension(GithubFormatExtension extension);

    String getBranch();

    void setBranch(String branch);

    void setBranch(int index);

    String getMessage();

    void setMessage(String branch);

    String getPath();

    void setPath(String path);

    CommitData getCommitData();

}
