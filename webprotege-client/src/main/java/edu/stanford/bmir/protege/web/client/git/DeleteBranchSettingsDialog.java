package edu.stanford.bmir.protege.web.client.git;

import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.library.dlg.*;
import edu.stanford.bmir.protege.web.shared.git.CommitFormatExtension;

import javax.annotation.Nonnull;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public class DeleteBranchSettingsDialog {

    public DeleteBranchSettingsDialog(){
    }

    private static CommitFormatExtension lastExtension = CommitFormatExtension.owl;

    public static void showDialog(final DeleteRemoteBranchHandler handler, String repoURI, String token) {
        final DeleteBranchSettingsView view = new DeleteBranchSettingsViewImpl(repoURI, token);
        view.setBranch(0);

        WebProtegeOKCancelDialogController<DeleteRemoteBranchData> controller = new WebProtegeOKCancelDialogController<DeleteRemoteBranchData>("Delete Branch") {

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
            public DeleteRemoteBranchData getData() {
                return view.getDeleteRemoteBranchData();
            }
        };
        controller.setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<DeleteRemoteBranchData>() {
            @Override
            public void handleHide(DeleteRemoteBranchData data, WebProtegeDialogCloser closer) {
                closer.hide();
                handler.handleDeleteBranch(data);
            }
        });
        WebProtegeDialog<DeleteRemoteBranchData> dlg = new WebProtegeDialog<DeleteRemoteBranchData>(controller);
        dlg.show();
    }


}
