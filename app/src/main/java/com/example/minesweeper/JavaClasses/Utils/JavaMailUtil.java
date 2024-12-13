package com.example.minesweeper.JavaClasses.Utils;


import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


// https://rohanpillai.weebly.com/blog/sending-a-mail-using-pure-java-basic-tutorial
// https://forums.oracle.com/ords/apexds/post/getting-error-as-java-net-unknownhostexception-smtp-gmail-c-9394

public class JavaMailUtil {
    private final String
            emailTO,
            emailFROM, hostUsername, hostPassword, host;

    private String
            emailSubject, emailBody;

    private Properties properties;
    private Session session;
    // android.os.NetworkOnMainThreadException; main thread is not allowed to perform network operations
    private ExecutorService backGroundExecutor;


    public JavaMailUtil(final String hostEmail, final String hostPassword, final String emailTO) {
        this.emailTO = emailTO;

        // From local.properties
        this.emailFROM = hostEmail;
        this.hostUsername = hostEmail;
        this.hostPassword = hostPassword;

        this.host = "smtp.gmail.com"; // Sending the email through SMTP of Gmail

        /*Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            System.out.println(key + "=" + env.get(key));
        }*/

        checkEnvironmentVariables();

        this.backGroundExecutor = Executors.newSingleThreadExecutor();
    }

    private void checkEnvironmentVariables() {
        if (this.hostUsername == null || this.hostPassword == null) throw new RuntimeException("Environment variables not set");
    }

    private void createProperties() {
        this.properties = new Properties();

        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", this.host);
        properties.put("mail.smtp.port", "587"); // Port 587 is used to send emails using SMTP with STARTTLS, cyphering the connection

    }
    // Configuration of a SMTP session using JavaMail
    // PasswordAuthentication provides the necessary credentials to authenticate the user (me)
    private void createSession() {
        this.session = Session.getInstance(this.properties,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(hostUsername, hostPassword);
            }
        });
    }

    public void setEmailSubject(String subject) {
        this.emailSubject = subject;

    }

    public void setEmailBody(String body) {
        this.emailBody = body;
    }

    public void sendEmailInBackground() {
        backGroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        if (ableToSendEmail()) {
            createProperties();
            createSession();

            try {
                Message message = new MimeMessage(this.session);

                message.setFrom(new InternetAddress(this.emailFROM)); // From who
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.emailTO)); // To who
                message.setSubject(this.emailSubject); // Subject
                message.setText(this.emailBody); // Body

                Transport.send(message);
            } catch (MessagingException e) {
                System.err.println("Error while trying to send the email");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Insufficient data to send the email");
        }
    }

    private boolean ableToSendEmail() {
        return this.emailTO != null && !this.emailTO.isEmpty()
                && this.emailFROM != null && !this.emailFROM.isEmpty()
                && this.emailSubject != null && this.emailBody != null;
    }
}