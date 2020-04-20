<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Sign in</title>
	<link href="./styles/styles.css" rel="stylesheet">
</head>
<body>
	<fieldset style="width:25%;">
	<h1>Sign in</h1>
	<% String message = (String)request.getAttribute("message");
	if(message == null) {
		message = "   ";
	} %>
	<p style="color:red"><% out.println(message);%></p>
	<form name="myForm" action="LoginDetails" method="post">
		Gmail Id: <input type="email" name="id"><br><br>
		Password: <input type="password" name="pwd"><br><br>
		<input class="button" type="submit" value="Sign in">
	</form>
	</fieldset>
</body>
</html>