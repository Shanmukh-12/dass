package spring.orm.util;

import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSend {
	private static final Logger logger = LoggerFactory.getLogger(MailSend.class);

	private static final String EMAIL_USERNAME = "yathindranikshipth@gmail.com";
	private static final String EMAIL_PASSWORD = "nslq npgk ripj lglk";
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";

	public void sendOTPMail(String recipient, String otp) {
		try {
			Session session = createMailSession();

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_USERNAME));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject("Forget Password");
			message.setText(
					"OTP TO RESET PASSWORD: " + otp + "\nPlease use it quickly as it is active for 3 minutes only");

			Transport.send(message);

			logger.info("OTP email sent to: {}", recipient);
		} catch (Exception e) {
			logger.error("Failed to send OTP email to: {}", recipient, e);
		}
	}

	public String generateOTP() {
		int otpLength = 6; // Length of the OTP

		// Generate the OTP
		StringBuilder otp = new StringBuilder();
		while (otp.length() < otpLength) {
			UUID uuid = UUID.randomUUID();
			otp.append(uuid.toString().replaceAll("-", "").substring(0, otpLength - otp.length()));
		}

		return otp.toString();
	}

	private Session createMailSession() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", SMTP_PORT);

		return Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
			}
		});
	}
}
