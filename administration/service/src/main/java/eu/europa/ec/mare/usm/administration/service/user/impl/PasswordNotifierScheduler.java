package eu.europa.ec.mare.usm.administration.service.user.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.List;

/**
 * J2EE scheduled singleton for triggering the sending of
 * notifications (or reminders) to users whose password is
 * about to expire.
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PasswordNotifierScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordNotifierScheduler.class);

    @EJB
    private PasswordExpiryNotifier expiryReminder;

    public PasswordNotifierScheduler() {
    }

    /**
     * Triggers the sending of notifications (or reminders) to users
     * whose password is about to  expire.
     * <p>
     * Scheduled job running every 6 hours on the hour.
     */
    @Schedule(dayOfWeek = "*", hour = "*/6", minute = "0")
    public void triggerNotifications() {
        LOGGER.debug("triggerNotifications() - (ENTER)");

        List<String> userNames = expiryReminder.findUsersToNotify();

        for (String userName : userNames) {
            expiryReminder.notifyUser(userName);
        }

        LOGGER.debug("triggerNotifications() - (LEAVE)");
    }

}
