<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Email Client</title>
</head>
<body>
<h1> Email Client</h1>
<form name = "myForm" action="index" method="post">
	<input type="button" value="Compose" name="compose" onclick="openPage('compose.jsp')"/>
	<input type="button" value="Inbox" name="inbox" onclick="openPage('inbox.jsp')"/>
</form>
<script type="text/javascript">
	function openPage(pageURL)
	{
		window.location.href = pageURL;
	}
</script>
</body>
</html>