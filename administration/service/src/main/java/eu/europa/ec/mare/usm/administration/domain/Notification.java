package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds details of a notification e-mail
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String recipient;
    private String subject;
    private String content;

    public Notification() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Notification{" + "sender=" + sender + ", recipient="
                + recipient + ", subject=" + subject + ", content=" + content
                + '}';
    }

}
