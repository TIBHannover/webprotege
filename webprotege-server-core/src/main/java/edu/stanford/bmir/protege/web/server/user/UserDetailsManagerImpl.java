package edu.stanford.bmir.protege.web.server.user;

import edu.stanford.bmir.protege.web.shared.user.EmailAddress;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;
import edu.stanford.bmir.protege.web.shared.user.UserDetails;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge Stanford Center for Biomedical Informatics Research 06/02/15
 */
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final UserRecordRepository repository;

    private final Logger logger = LoggerFactory.getLogger(UserDetailsManagerImpl.class);

    @Inject
    public UserDetailsManagerImpl(UserRecordRepository userRecordRepository) {
        this.repository = checkNotNull(userRecordRepository);
    }

    @Override
    public List<UserId> getUserIdsContainingIgnoreCase(String userName, int limit) {
        return repository.findByUserIdContainingIgnoreCase(userName, limit);
    }

    @Override
    public Optional<UserId> getUserIdByEmailAddress(EmailAddress emailAddress) {
        if (emailAddress.getEmailAddress().isEmpty()) {
            return Optional.empty();
        }
        Optional<UserRecord> record = repository.findOneByEmailAddress(emailAddress.getEmailAddress());
        return record.map(UserRecord::getUserId);
    }

    @Override
    public Optional<UserId> getUserIdByPersonalAccessToken(PersonalAccessToken personalAccessToken) {
        if (personalAccessToken.getPersonalAccessToken().isEmpty()) {
            return Optional.empty();
        }
        Optional<UserRecord> record = repository.findOneByEmailAddress(personalAccessToken.getPersonalAccessToken());
        return record.map(UserRecord::getUserId);
    }

    @Override
    public Optional<UserDetails> getUserDetails(UserId userId) {
        if (userId.isGuest()) {
            return Optional.of(UserDetails.getGuestUserDetails());
        }
        Optional<UserRecord> record = repository.findOne(userId);
        return record.map(userRecord ->
                                  UserDetails.getUserDetails(userId,
                                                             userId.getUserName(),
                                                             Optional.of(userRecord.getEmailAddress()),
                                                             Optional.of(userRecord.getPersonalAccessToken())));
    }

    @Override
    public Optional<String> getEmail(UserId userId) {
        if (userId.isGuest()) {
            return Optional.empty();
        }
        Optional<UserRecord> record = repository.findOne(userId);
        if (!record.isPresent()) {
            return Optional.empty();
        }
        String emailAddress = record.get().getEmailAddress();
        if (emailAddress.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(emailAddress);
    }

    @Override
    public void setEmail(UserId userId, String email) {
        checkNotNull(userId);
        checkNotNull(email);
        logger.info("Received request to set email address ({}) for user ({})", email, userId.getUserName());
        if (userId.isGuest()) {
            logger.info("Specified user is the guest user.  Not setting email address.");
            return;
        }
        Optional<UserRecord> record = repository.findOne(userId);
        if (!record.isPresent()) {
            logger.info("Specified user ({}) does not exist.", userId.getUserName());
            return;
        }
        Optional<UserRecord> recordByEmail = repository.findOneByEmailAddress(email);
        if (recordByEmail.isPresent()) {
            logger.info("Account with specified email address ({}) already exists", email);
            return;
        }
        UserRecord theRecord = record.get();
        UserRecord replacement = new UserRecord(
                theRecord.getUserId(),
                theRecord.getRealName(),
                email,
                theRecord.getPersonalAccessToken(),
                theRecord.getAvatarUrl(),
                theRecord.getSalt(),
                theRecord.getSaltedPasswordDigest()
        );
        repository.delete(userId);
        repository.save(replacement);
        logger.info("Email address for {} changed to {}", userId.getUserName(), email);
    }

    @Override
    public Optional<UserId> getUserByUserIdOrEmail(String userNameOrEmail) {
        Optional<UserRecord> byUserId = repository.findOne(UserId.getUserId(userNameOrEmail));
        if (byUserId.isPresent()) {
            return Optional.of(byUserId.get().getUserId());
        }
        Optional<UserRecord> byEmail = repository.findOneByEmailAddress(userNameOrEmail);
        return byEmail.map(UserRecord::getUserId);
    }

    @Override
    public Optional<String> getToken(UserId userId) {
        if (userId.isGuest()) {
            return Optional.empty();
        }
        Optional<UserRecord> record = repository.findOne(userId);
        if (!record.isPresent()) {
            return Optional.empty();
        }
        String personalAccessToken = record.get().getPersonalAccessToken();
        if (personalAccessToken.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(personalAccessToken);
    }

    @Override
    public void setToken(UserId userId, String token) {
        checkNotNull(userId);
        checkNotNull(token);
        logger.info("Received request to set personal access token ({}) for user ({})", token, userId.getUserName());
        if (userId.isGuest()) {
            logger.info("Specified user is the guest user.  Not setting personal access token.");
            return;
        }
        Optional<UserRecord> record = repository.findOne(userId);
        if (!record.isPresent()) {
            logger.info("Specified user ({}) does not exist.", userId.getUserName());
            return;
        }
        Optional<UserRecord> recordByEmail = repository.findOneByPersonalAccessToken(token);
        if (recordByEmail.isPresent()) {
            logger.info("Account with specified personal access token ({}) already exists", token);
            return;
        }
        UserRecord theRecord = record.get();
        UserRecord replacement = new UserRecord(
                theRecord.getUserId(),
                theRecord.getRealName(),
                theRecord.getEmailAddress(),
                token,
                theRecord.getAvatarUrl(),
                theRecord.getSalt(),
                theRecord.getSaltedPasswordDigest()
        );
        repository.delete(userId);
        repository.save(replacement);
        logger.info("Personal access token for {} changed to {}", userId.getUserName(), token);
    }

    @Override
    public Optional<UserId> getUserByUserIdOrToken(String userNameOrToken) {
        Optional<UserRecord> byUserId = repository.findOne(UserId.getUserId(userNameOrToken));
        if (byUserId.isPresent()) {
            return Optional.of(byUserId.get().getUserId());
        }
        Optional<UserRecord> byEmail = repository.findOneByPersonalAccessToken(userNameOrToken);
        return byEmail.map(UserRecord::getUserId);
    }



}
