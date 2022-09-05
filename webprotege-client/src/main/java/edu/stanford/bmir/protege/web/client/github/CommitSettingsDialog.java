package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.download.DownloadFormatExtensionHandler;
import edu.stanford.bmir.protege.web.client.download.DownloadSettingsView;
import edu.stanford.bmir.protege.web.client.download.DownloadSettingsViewImpl;
import edu.stanford.bmir.protege.web.client.library.dlg.*;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;

import javax.annotation.Nonnull;

/**
 * Author Nenad Krdzavac <br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 05.09.2022.<br>
 *
 */
public class CommitSettingsDialog {

    private static GithubFormatExtension lastExtension = GithubFormatExtension.owl;

    public static void showDialog(final CommitFormatExtensionHandler handler) {

        final CommitSettingsView view = new CommitSettingsViewImpl();
        view.setGithubFormatExtension(lastExtension);

        WebProtegeOKCancelDialogController<GithubFormatExtension> controller = new WebProtegeOKCancelDialogController<GithubFormatExtension>("Commit project") {

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
            public GithubFormatExtension getData() {
                return view.getGithubFormatExtension();
            }
        };
        controller.setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<GithubFormatExtension>() {
            @Override
            public void handleHide(GithubFormatExtension data, WebProtegeDialogCloser closer) {
                closer.hide();
                lastExtension = data;
                handler.handleCommit(data);
            }
        });
        WebProtegeDialog<GithubFormatExtension> dlg = new WebProtegeDialog<GithubFormatExtension>(controller);
        dlg.show();
    }
}
