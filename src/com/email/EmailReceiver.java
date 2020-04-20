package com.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

public class EmailReceiver {
	String directory;
 	//to establish secured server connection
	private Properties getServerProperties(String protocol, String host, String port) {
        Properties properties = new Properties();
 
        //IMAP or POP3 server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        //Secure connection establishment setting(SSL setting)
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
 
        return properties;
    }
 
	//To download new messages and fetches details for each message.
    public String downloadEmails(String protocol, String host, String port, String userName, String password) {
        
    	//to store the secured connection details in the form of key-value pairs
    	Properties properties = getServerProperties(protocol, host, port);
    	
    	//to establish a mail session using the set properties
        Session session = Session.getDefaultInstance(properties);
        
        String inboxMsg = ""; //to store the fetched messages

        try {
        	//directory where all mail attachments get stored
        	directory = System.getProperty("user.home")+File.separator+"EmailAttachments";
            if(Files.notExists(Paths.get(directory))) {
            	Files.createDirectory(Paths.get(directory)); //to create the directory if not present
            }
            
            inboxMsg += "<br><h4>Email Attachments will get stored in:</h4><br>" + directory + "<br><br>"; 
            //to connect to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);
 
            //to open the inbox folder in read-only mode
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
 
            //to fetch the new and unread messages from server
            Message[] messages = folderInbox.search(
            	    new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            
            //to display 20 latest unread messages one after the other
            for (int i = messages.length-1; i > messages.length -21; i--) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom(); 
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg.getRecipients(RecipientType.TO));
                String ccList = parseAddresses(msg.getRecipients(RecipientType.CC));
                String sentDate = msg.getSentDate().toString();
 
                String contentType = msg.getContentType(); //to store the content type
                String messageContent = ""; //to store the message content
                String attachedFiles = ""; //to store the mail attachments
                
                try {
                	//if content type is plain text or html, convert to string and store it
                    if (contentType.contains("TEXT/PLAIN") || contentType.contains("TEXT/HTML")) {
                    	messageContent = msg.getContent().toString();
                    }
                    else if (contentType.contains("multipart")) { //if content type is multipart
                        Multipart multiPart = (Multipart) msg.getContent(); //get message content
                        int numberOfParts = multiPart.getCount(); //get the number of parts present in the message content
                        for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        	MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) { //check for attachments
                            	String fileName = part.getFileName();
                                attachedFiles += fileName + ", ";
                                part.saveFile(directory + File.separator + fileName); //save the attachments bearing the extracted filename in the set directory
                            }
                            else {
                                    messageContent = part.getContent().toString(); //else convert to string and store it
                            }
                        }
                    }
                } catch (Exception ex) { //to catch any exception while extracting and converting the message content
                	messageContent = "[Error downloading content]";
                	ex.printStackTrace();
                }
                
                //concatenate all the extracted content
                inboxMsg += "<br><br><fieldset style=\"border-width:5px\">";
                inboxMsg += "<legend style=\"font-size:20px\">Message #" + (i + 1) + "</legend>";
                inboxMsg += "<br>&nbsp<b>From:</b> " + from;
                inboxMsg += "<br>&nbsp<b>To:</b> " + toList;
                inboxMsg += "<br>&nbsp<b>CC:</b> " + ccList;
                inboxMsg += "<br>&nbsp<b>Subject:</b> " + subject;
                inboxMsg += "<br>&nbsp<b>Sent Date:</b> " + sentDate;
                inboxMsg += "<br>&nbsp<b>Message:</b> " + messageContent;
                inboxMsg += "<br>&nbsp<b>Attachments:</b> " + (attachedFiles.isEmpty() ? "No attachments" : attachedFiles.substring(0, attachedFiles.length() - 2)) + "</fieldset>";
            }
            
            folderInbox.close(false); //close the inbox folder
            store.close(); //close the store
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
			ex.printStackTrace();
		}
		return inboxMsg;
    }
    
    //Returns a list of addresses in String format separated by comma
    private String parseAddresses(Address[] address) {
        String listAddress = "";
 
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }
 
    //email receiver
    public String receive(String mailId, String pwd) {
        String protocol = "imap";
        String host = "imap.gmail.com";
        String port = "993";
        String userName = mailId;
        String password = pwd;
        EmailReceiver receiver = new EmailReceiver(); 
        return receiver.downloadEmails(protocol, host, port, userName, password);
    }
}
