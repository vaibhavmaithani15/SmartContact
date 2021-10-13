package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class EmailService {
	public boolean sendEmail(String subject, String message,String to){

        // variable for gmail;
        boolean flag=false;
        String from="rahul.maithani1@gmail.com";
        String host = "smtp.gmail.com";
        // get the system property
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES " + properties);

        // setting import information to properties object

        // host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step1: to get the session object
        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // TODO Auto-generated method stub
                return new PasswordAuthentication("rahul.maithani1@gmail.com", "01124817416");
            }
        });
        session.setDebug(true);
//	Step2: Compose the message[text,multi media]
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            // from
            mimeMessage.setFrom(from);
            // add recipiant
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // adding subject to message
            mimeMessage.setSubject(subject);

			// adding text to message
//			mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");

            // Step 3: Send the message using Transport class
            Transport.send(mimeMessage);
            System.out.println("Message Send Successfully.........");
            flag=true;

        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;

    }

}
