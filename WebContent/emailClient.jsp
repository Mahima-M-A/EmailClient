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
	<%--to add to session --%>
	<%  String mailId, pwd;
		if(request.getAttribute("mailId") != null) {
			mailId = (String)request.getAttribute("mailId");
			pwd = (String)request.getAttribute("pwd");
			session.setAttribute("mailId", mailId);
			session.setAttribute("pwd",pwd);
		}
		else {
			mailId = (String)session.getAttribute("mailId");
			pwd = (String)session.getAttribute("pwd");
		} %>
	
	<h3>Welcome <i><% out.println(mailId); %></i></h3><br><br>
	<input type="button" value="Compose" name="compose" onclick="openPage('compose.jsp')"/>
	<input type="button" value="Inbox" name="inbox" onclick="openPage('inbox.jsp')"/>
	<input type="button" value="Sign out" name="signout" onclick="openPage('index.jsp')"/>
	<script type="text/javascript">
		function openPage(pageURL) {
			window.location.href = pageURL;
		}
	</script>
</body>
</html>