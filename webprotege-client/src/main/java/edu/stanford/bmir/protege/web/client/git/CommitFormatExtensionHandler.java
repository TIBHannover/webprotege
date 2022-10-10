package edu.stanford.bmir.protege.web.client.git;

/**
 * Author Nenad Krdzavac <br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 05.09.2022.<br>
 *
 */
public interface CommitFormatExtensionHandler {

    void handleCommit(CommitData commitData);
}
