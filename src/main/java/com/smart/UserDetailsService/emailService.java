package com.smart.UserDetailsService;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class emailService {

	/*
	 * String message = "Hello, Dear, this is message for security check ."; String
	 * subject = "CodersArea: Confirmation"; String to =
	 * "tripathikauntey2000@gmail.com";
	 */

	public boolean sendEmail(String message, String to, String subject) {
		boolean f = false;
		// Variable for gmail
		String from = "Kaunteyrocks2388@gmail.com";
		String host = "smtp.gmail.com";
		// get the system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES " + properties);
		// setting important information to properties object
		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// step 2 get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication()

			{

				return new PasswordAuthentication("kauntey2332@gmail.com", "hjvumppmoyvjzoun");
			}

		});

		session.setDebug(true);

		// compose message

		MimeMessage m = new MimeMessage(session);

		try {
			// from email
			m.setFrom(from);

			// adding receipient

			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message

			m.setSubject(subject);

			// Adding text
			m.setText(message);

			// step 3 send message

			Transport.send(m);
			System.out.println("sent successfully !!");

			f = true;
		}

		catch (Exception e) {

			e.printStackTrace();
		}
		return f;

	}
}
