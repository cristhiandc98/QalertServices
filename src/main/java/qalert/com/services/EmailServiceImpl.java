package qalert.com.services;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IEmailService;
import qalert.com.models.email.SendEmailRequest;
import qalert.com.models.generic.Response2;
import qalert.com.utils.consts.EnvironmentConst;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements IEmailService{

	@Autowired
	private Environment env;

	public Response2<String> sendSimpleEmail(SendEmailRequest request, String mssgOk){
		Response2<String> salida = null;

		Properties props = new Properties();  
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");  

		try {
			String nombreApp = env.getRequiredProperty(EnvironmentConst.APP_NAME)
				,remitente = env.getRequiredProperty(EnvironmentConst.GOOGLE_EMAIL_REMITENTE)
				,contrasenia = env.getRequiredProperty(EnvironmentConst.GOOGLE_EMAIL_CONTRASENIA);

			Session session = Session.getDefaultInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(remitente, contrasenia);
				}
			});

			Message msg = new MimeMessage(session);
			
			msg.setFrom(new InternetAddress(remitente, nombreApp));
			msg.addRecipient(Message.RecipientType.TO,new InternetAddress(request.getTo()));
			msg.setSubject(request.getSubject());
			msg.setText(request.getBody());
			
			Transport.send(msg);
			
			salida = new Response2<String>(HttpStatus.OK, (mssgOk == null ? "¡Correo enviado exitosamente!" : mssgOk), true);
		} catch (AddressException e) {
			salida = new Response2<>(e);
		} catch (MessagingException e) {
			salida = new Response2<>(e);
		} catch (UnsupportedEncodingException e) {
			salida = new Response2<>(e);
		}
		return salida;
	}
	
}
