<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Compose</title>
</head>
<body>
<h1> Compose Mail</h1>
<form name = "myForm1" action="compose.jsp" method="post">
From:<input type="text" name="from" size="50"/><br><br>
To  : <input type="text" name="recipient address" size="50"/><br><br>
Subject:<input type="text" name="subject" size="50"/><br><br>
Body:<TEXTAREA NAME="body" ROWS="5" cols="50" ></TEXTAREA>
<br><br>
<input type="button" value="Back" name="back" onclick="openPage('index.jsp')"/>
<input type="submit" value="Send" >
</form>
<script type="text/javascript">
function openPage(pageURL)
{
window.location.href = pageURL;
}
</script>

</body>
</html>