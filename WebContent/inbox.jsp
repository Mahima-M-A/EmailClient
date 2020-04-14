<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.email.EmailReceiver" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Inbox</title>
</head>
<body>
<h1> Inbox</h1>
	<input type="button" value="Back" name="back" onclick="openPage('emailClient.jsp')"/>
	<input type="button" value="Compose" name="compose" onclick="openPage('compose.jsp')"/>
	<input type="button" value="Logout" name="inbox" onclick="openPage('index.jsp')"/>
	<%  String mailId = (String)session.getAttribute("mailId");
		String pwd = (String)session.getAttribute("pwd");
		EmailReceiver er = new EmailReceiver();
		out.println(er.receive(mailId, pwd)); %>
		
	<script type="text/javascript">
		document.addEventListener()
		function openPage(pageURL) {
			window.location.href = pageURL;
		}
	</script>

</body>
</html>