<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Sign in</title>
</head>
<body>
	<h1>Sign in</h1>
	<form name="myForm" action="LoginDetails" method="post">
		Gmail Id: <input type="email" name="id"><br><br>
		Password: <input type="password" name="pwd"><br><br>
		<input type="submit" value="Sign in">
	</form>
</body>
</html>