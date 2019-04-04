package eu.europa.ec.mare.usm.administration.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.Notification;

/**
 * Sends Notification messages.
 */
public class NotificationSender {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSender.class);
  private static final String PROPERTIES = "/notification.properties";

  /**
   * Creates a new instance.
   */
  public NotificationSender() 
  {
  }

  /**
   * Sends the provided Notification message,
   *
   * @param msg the Notification message
   *
   * @throws MessagingException in case message fails to be sent
   */
  public void sendNotification(Notification msg)
  throws MessagingException 
  {
    LOGGER.debug("sendNotification() - (ENTER)");
    Session session = getSession();

    Message message = new MimeMessage(session);
    if (msg.getSender() != null) {
      message.setFrom(new InternetAddress(msg.getSender()));
    }
    message.setRecipient(Message.RecipientType.TO,
                         new InternetAddress(msg.getRecipient()));
    if (msg.getSubject() != null) {
      message.setSubject(msg.getSubject());
    }
    message.setText(msg.getContent());
    
    Transport.send(message);

    LOGGER.debug("sendNotification() - (LEAVE)");
  }

  private Session getSession() 
  throws MessagingException 
  {
    LOGGER.debug("getSession() - (ENTER)");
    InitialContext ctx = null;
    Session ret;

    Properties prop = getProperties();
    String smtpSession = prop.getProperty("smtp_session");

    try {
      ctx = new InitialContext();

      ret = (Session) ctx.lookup(smtpSession);

    } catch (NamingException exc) {
      throw new MessagingException("Failed to lookup mail session " + smtpSession, exc);
    } finally {
      if (ctx != null) {
        try {
          ctx.close();
        } catch (NamingException exc) {
          LOGGER.error("Error closing context", exc);
        }
      }
    }

    LOGGER.debug("getSession() - (LEAVE)");
    return ret;
  }

  private Properties getProperties() 
  {
    Properties ret = new Properties();

    InputStream is = getClass().getResourceAsStream(PROPERTIES);
    try {
      if (is == null) {
        throw new IOException("Resource " + PROPERTIES + " not found");
      } else {
        ret.load(is);
      }
    } catch (IOException exc) {
      LOGGER.error("Failed to load " + PROPERTIES, exc);
    }
    
    return ret;
  }
}
