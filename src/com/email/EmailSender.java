package com.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class EmailSender
 */
@WebServlet("/EmailSender")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50) //as servlet expects requests to be made using the multipart/form-data MIME type
public class EmailSender extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private String host;
    private String port;
    private String sender;
    private String password;

    //to be run first
    public void init() {
    	ServletContext context = getServletContext(); //to extract information from web.xml
    	host = context.getInitParameter("host");
    	port = context.getInitParameter("port");
    }
    
    //to send the mail to a single recipient
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		sender = (String)request.getSession().getAttribute("mailId");
		password = (String)request.getSession().getAttribute("pwd");
		String recipient = request.getParameter("recipient address");
		String subject = request.getParameter("subject");
		String body = request.getParameter("body");
		
		ArrayList<File> attachedFiles = saveAttachedFiles(request); //to store the attached files
		
		PrintWriter out = response.getWriter();
		try {
			
			//to set the SMTP server settings
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			
			//to authenticate the mailId and password
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sender, password);
				}
			};
			
			//to establish a mail session using the set properties
			Session session = Session.getInstance(properties, auth);
			
			//to set the fields of the email to be sent
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject(subject);
			message.setText(body);
			message.setSentDate(new Date());
			
			//creates Mime body part for message content
			MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(body, "text/html");
	 
	        //creates multipart for attachments
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	 
	        //to add attachments
	        if(attachedFiles != null && attachedFiles.size() > 0) {
	            for (File file : attachedFiles) {
	                MimeBodyPart attachPart = new MimeBodyPart();
	 
	                try {
	                    attachPart.attachFile(file);
	                } catch (IOException ex) {
	                    ex.printStackTrace();
	                }
	 
	                multipart.addBodyPart(attachPart);
	            }
	        }
	 
	        //sets the multipart as e-mail's content
	        message.setContent(multipart);
			message.saveChanges();
			Transport.send(message);
			
			//to send data to the redirected jsp file
			request.setAttribute("mailId", sender);
			request.setAttribute("pwd", password);
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("emailClient.jsp");
	        requestDispatcher.forward(request, response);
	        
	        //to show an alert dialog box
			out.println("<script language=\"javascript\">");
			out.println("alert('Email was successfully sent')");
			out.println("location='emailClient.jsp';");
			out.println("</script>");
		}
		catch(Exception e) {
			e.printStackTrace();
			
			request.setAttribute("mailId", sender);
			request.setAttribute("pwd", password);
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("emailClient.jsp");
	        requestDispatcher.forward(request, response);
	        
			out.println("<script language=\"javascript\">");
			out.println("alert('Email was not sent')");
			out.println("location='emailClient.jsp';");
			out.println("</script>");
		}
	}

	//to extract and return the mail attachments
	private ArrayList<File> saveAttachedFiles(HttpServletRequest request) throws IOException, ServletException {
		ArrayList<File> attachedFiles = new ArrayList<>();
		byte[] buffer = new byte[4096];
		int readBytes = -1;
		Collection<Part> multiparts = request.getParts();
		if (multiparts.size() > 0) {
            for (Part p : request.getParts()) {
                String fileName = extractFileName(p);
                if (fileName == null || fileName.equals("")) {
                    continue;
                }
                 
                File saveFile = new File(fileName);
                FileOutputStream outputStream = new FileOutputStream(saveFile);
                 
                //saves uploaded file
                InputStream inputStream = p.getInputStream();
                while ((readBytes = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readBytes);
                }
                outputStream.close();
                inputStream.close();
                 
                attachedFiles.add(saveFile);
            }
        }
		return attachedFiles;
	}

	//to return the extracted file names
	private String extractFileName(Part p) {
		String content = p.getHeader("content-disposition");
        String[] items = content.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
		return null;
	}
}
