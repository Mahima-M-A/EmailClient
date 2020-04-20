package com.email;

import java.io.IOException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginDetails
 */
@WebServlet("/LoginDetails")
public class LoginDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//to login
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mailId = request.getParameter("id");
		String password = request.getParameter("pwd");
		boolean isValid = false;
		try {
			//to validate the entered mailID
			InternetAddress internetAddress = new InternetAddress(mailId);
			internetAddress.validate();
			
			//to set the SMTP server setting
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.port", "587");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			
			//to authenticate the entered mailID and password
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailId, password);
				}
			};
			
			//create a mail session
			Session session = Session.getInstance(properties, auth);
			try {
				Transport transport = session.getTransport("smtp");
				transport.connect(mailId, password);
				transport.close(); //is successful only if the mailID and password exist and are correct
				isValid = true;
			} catch (AuthenticationFailedException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
		} catch (AddressException e) {
			e.printStackTrace();
		}
		
		if(isValid) { //logs in if valid 
			request.setAttribute("mailId", mailId);
			request.setAttribute("pwd", password);
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("emailClient.jsp");
	        requestDispatcher.forward(request, response);
		}
		else {
			request.setAttribute("message", "Invalid credentials");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
	        requestDispatcher.forward(request, response);
		}
	}
}
