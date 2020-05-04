# EmailClient
This is a web app built using JavaMail api used to send and receive emails(It uses SMTP server for Gmail).

## Installation

```bash
git clone https://github.com/Mahima-M-A/EmailClient.git
```

Once cloned, the app may be run through appropriate softwares, such as eclipse.

## Usage

Open the app, and make sure it has the required external jar files(javax.mail.jar and servlet-api.jar(Tomcat server)) available in the WEB_INF/lib folder. Also make sure that the radio button for allowing less secure apps is turned ON in your gmail security settings before running this app. You can simply run the index.jsp file on server(Tomcat) to checkout the functionalities of this app.

## Functionalities

* Signs in only Gmail users on providing valid credentials, it also allows them to sign out
* Compose and send emails with or without an attachment to a recipient at a time
* Validates the recipient email address only with respect to domain but doesn't find out the validity or existence of the username in that domain
* Inbox displays the 20 latest unread messages(plain text, html or multipart) and the location of the folder on your system where the email attachments would be stored 


## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.


Please make sure to update tests as appropriate.
