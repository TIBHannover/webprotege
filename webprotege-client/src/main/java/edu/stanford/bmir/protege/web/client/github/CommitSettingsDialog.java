package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.library.dlg.*;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Author Nenad Krdzavac <br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 05.09.2022.<br>
 *
 */
public class CommitSettingsDialog {

    public CommitSettingsDialog(){
    }

    private static GithubFormatExtension lastExtension = GithubFormatExtension.owl;

    public static void showDialog(final CommitFormatExtensionHandler handler, String repoURI, String token) {
        final CommitSettingsView view = new CommitSettingsViewImpl(repoURI, token);
        view.setGithubFormatExtension(lastExtension);
        view.setBranch(0);

        WebProtegeOKCancelDialogController<CommitData> controller = new WebProtegeOKCancelDialogController<CommitData>("Commit project") {

            @Override
            public Widget getWidget() {
                return view.asWidget();
            }

            @Nonnull
            @Override
            public java.util.Optional<HasRequestFocus> getInitialFocusable() {
                return view.getInitialFocusable();
            }

            @Override
            public CommitData getData() {
                return view.getCommitData();
            }
        };
        controller.setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<CommitData>() {
            @Override
            public void handleHide(CommitData data, WebProtegeDialogCloser closer) {
                closer.hide();
                lastExtension = data.getGfe();
                handler.handleCommit(data);
            }
        });
        WebProtegeDialog<CommitData> dlg = new WebProtegeDialog<CommitData>(controller);
        dlg.show();
    }


}
