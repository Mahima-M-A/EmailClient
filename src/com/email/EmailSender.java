package com.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
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
	        if(isAddressValid(recipient)) {
	        	Transport.send(message);
	        } else {
	        	throw new Exception();
	        }
	        
	        //to show an alert dialog box
	        out.println("<script language=\"javascript\">");
	        out.println("alert('Email was successfully sent')");
	        out.println("location='emailClient.jsp';");
	        out.println("</script>");
		}
		catch(Exception e) {
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
	
	// returns the list of mail exchangers
	private ArrayList getMX(String hostName) throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[]{"MX"});
		Attribute attr = attrs.get("MX");
		
		// if we don't have an MX record, try the machine itself
		if((attr == null) || (attr.size() == 0)) {
			attrs = ictx.getAttributes(hostName, new String[]{"A"});
	        attr = attrs.get("A");
	        if( attr == null ) 
	             throw new NamingException("No match for name '" + hostName + "'");
		}
	    // Return them as an array list
		ArrayList res = new ArrayList();
		NamingEnumeration en = attr.getAll();
		while(en.hasMore()) {
			String x = (String) en.next();
			String f[] = x.split(" ");
			if (f[1].endsWith(".")) {
				f[1] = f[1].substring(0, (f[1].length() - 1));
			}
			res.add(f[1]);
		}
		return res;
	}
	
	public boolean isAddressValid(String address) {
		int pos = address.indexOf('@');
		// If the address does not contain an '@', it's not valid
		if (pos == -1) return false;
		
		// Isolate the domain/machine name and get a list of mail exchangers
		String domain = address.substring(++pos);
		ArrayList mxList = null;
		try {
			mxList = getMX(domain);
		} catch (NamingException ex) {
			return false;
		}
		
		// if no mail exchanger is found then the domain is invalid
		if(mxList.size() == 0) return false;
		
		return true;
	}
}
