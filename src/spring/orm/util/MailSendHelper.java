package spring.orm.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.orm.model.output.MailAppOutputModel;

public class MailSendHelper {
	private static final Logger logger = LoggerFactory.getLogger(MailSendHelper.class);

	private static final String EMAIL_USERNAME = "yathindranikshipth@gmail.com";
	private static final String EMAIL_PASSWORD = "nslq npgk ripj lglk";
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";

	public static void sendBookingEmail(HttpServletRequest request, HttpServletResponse response,
			MailAppOutputModel app, String userMail) throws Exception {
		// Set up model attributes with the variables for JSP replacements

		// Resolve JSP view
		// InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		// viewResolver.setPrefix("/WEB-INF/views/");
		// viewResolver.setSuffix(".jsp");
		String viewName = "BookingMailTemplete";
		request.setAttribute("appointment", app);
		// Render JSP
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/views/" + viewName + ".jsp");
		StringWriter stringWriter = new StringWriter();
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
			@Override
			public PrintWriter getWriter() throws IOException {
				return new PrintWriter(stringWriter);
			}
		};
		requestDispatcher.include(request, responseWrapper);
		String renderedHtml = stringWriter.toString();
		logger.debug("Rendered HTML for booking email:\n{}", renderedHtml);

		Session session = createMailSession();
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_USERNAME));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(userMail));
			message.setSubject("Doctor Appointment Booking Details");
			message.setContent(renderedHtml, "text/html");

			Transport.send(message);

			logger.info("Booking email sent to: {}", userMail);
		} catch (Exception e) {
			logger.error("Failed to send booking email to: {}", userMail, e);
		}
	}

	public void sendRefundMail(String email, String amount) {
		Session session = createMailSession();

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_USERNAME));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("Refund Notification");
			message.setText("Your appointment has been suspended, and the amount " + amount + " has been refunded.");

			Transport.send(message);
			logger.info("Refund email sent to: {}", email);
		} catch (Exception e) {
			logger.error("Failed to send refund email to: {}", email, e);
		}
	}

	private static Session createMailSession() {
		Properties properties = new Properties();
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
			}
		});

		return session;
	}

	public static void sendEmailTestBooking(HttpServletRequest request, HttpServletResponse response, String email,
			String content) throws ServletException, IOException {
		// Resolve JSP view

		String viewName = "TestBookingMailTemplate";
		request.setAttribute("booktest", content);
		// Render JSP
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/views/" + viewName + ".jsp");
		StringWriter stringWriter = new StringWriter();
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
			@Override
			public PrintWriter getWriter() throws IOException {
				return new PrintWriter(stringWriter);
			}
		};
		requestDispatcher.include(request, responseWrapper);
		String renderedHtml = stringWriter.toString();
		logger.debug("Rendered HTML for test booking email:\n{}", renderedHtml);

		Session session = createMailSession();
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_USERNAME));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("Doctor Appointment Booking Details");
			message.setContent(renderedHtml, "text/html");

			Transport.send(message);

			logger.info("Test booking email sent to: {}", email);
		} catch (Exception e) {
			logger.error("Failed to send test booking email to: {}", email, e);
		}
	}
}