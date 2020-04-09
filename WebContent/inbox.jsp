<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Inbox</title>
</head>
<body>
<h1> Inbox</h1>
<input type="button" value="Back" name="back" onclick="openPage('index.jsp')"/>
<input type="button" value="Compose" name="compose" onclick="openPage('compose.jsp')"/>

<script type="text/javascript">
function openPage(pageURL)
{
window.location.href = pageURL;
}
</script>

</body>
</html>