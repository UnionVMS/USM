package eu.europa.ec.mare.usm.administration.service.user.impl;

import eu.europa.ec.mare.usm.administration.domain.Notification;
import eu.europa.ec.mare.usm.administration.service.NotificationBuilder;
import eu.europa.ec.mare.usm.administration.service.NotificationSender;
import eu.europa.ec.mare.usm.information.entity.UserEntity;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Reminds/notifies users via e-mail of the pending expiry of
 * their password.
 */
@Stateless
public class PasswordExpiryNotifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordExpiryNotifier.class);
    private static final String PASSWORD_SUBJECT = "Password";
    private static final String RENEWAL_REMINDER = "password.renewalReminder";
    private static final long ONE_DAY = (1000L * 60 * 60 * 24);

    @EJB
    private PolicyProvider policyProvider;

    @Inject
    private UserJdbcDao jdbcDao;

    @Inject
    private UserJpaDao jpaDao;

    NotificationSender sender;

    public PasswordExpiryNotifier() {
        sender = new NotificationSender();
    }

    /**
     * Finds the userName of all active (and enabled) users whose
     * password is about to expire and who should and could be
     * notified/reminded via e-mail.
     *
     * @return the possibly empty list of the matching user names
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<String> findUsersToNotify() {
        LOGGER.debug("findUsersToNotify() - (ENTER)");

        List<String> ret = Collections.EMPTY_LIST;

        Properties policy = policyProvider.getProperties(PASSWORD_SUBJECT);
        int days = policyProvider.getIntProperty(policy, RENEWAL_REMINDER, 0);

        if (days != 0) {
            Date expiringBefore = new Date(System.currentTimeMillis() +
                    (days * ONE_DAY));
            ret = jdbcDao.findByPasswordExpiry(expiringBefore);
        }

        LOGGER.debug("findUsersToNotify() - (LEAVE)");
        return ret;
    }

    /**
     * Reminds/notifies the specified user of the pending expiry of
     * her/his password.
     *
     * @param userName the userName of the user to be reminded/notified
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void notifyUser(String userName) {
        LOGGER.debug("remindUser(" + userName + ") - (ENTER)");

        UserEntity user = jpaDao.read(userName);
        if (user != null) {
            if (sendNotification(user)) {
                // Record notification to avoid spamming the user
                user.setExpiryNotification(new Date());
                jpaDao.update(user);
            }
        }

        LOGGER.debug("remindUser() - (LEAVE)");
    }

    /**
     * Builds and sends a password expiration reminder/notification.
     *
     * @param user the user whose password is about to expire
     * @return <i>true</i> if the notification was sent, <i>false</i>
     * otherwise
     */
    private boolean sendNotification(UserEntity user) {
        LOGGER.debug("sendNotification() - (ENTER)");

        Date passwordExpiry = user.getPasswordExpiry();
        String eMail = user.getPerson().getEMail();
        boolean ret = false;

        try {
            Notification msg = NotificationBuilder.buildReminder(eMail,
                    user.getUserName(),
                    passwordExpiry);
            sender.sendNotification(msg);

            LOGGER.info("Reminded user " + user.getUserName() +
                    " via e-mail to " + eMail +
                    " of the pending (" + passwordExpiry +
                    ") expiry of her/his password");
            ret = true;
        } catch (Exception exc) {
            LOGGER.error("Failed to remind user " + user.getUserName() +
                    " via e-mail to " + eMail +
                    " of the pending (" + passwordExpiry +
                    ") expiry of her/his password", exc);
        }

        LOGGER.debug("sendNotification() - (LEAVE)");
        return ret;
    }
}
