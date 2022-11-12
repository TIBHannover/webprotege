package edu.stanford.bmir.protege.web.client.git;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public interface DeleteBranchSettingsView extends IsWidget, HasInitialFocusable {

    String getBranch();

    void setBranch(String branch);

    void setBranch(int index);

    DeleteRemoteBranchData getDeleteRemoteBranchData();

}
