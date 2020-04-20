<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Compose</title>
	<link href="./styles/styles.css" rel="stylesheet">
</head>
<body>
	<fieldset style="width:70%">
		<h1> Compose Mail</h1>
		<form name = "myForm1" action="EmailSender" method="post" enctype="multipart/form-data">
			<%--to get from session --%>
			<% String mailId = (String)session.getAttribute("mailId");
				String pwd = (String)session.getAttribute("pwd"); %>
			
			To  : <input type="email" name="recipient address" size="50"/><br><br>
			Subject:<input type="text" name="subject" size="50"/><br><br>
			Body:<TEXTAREA NAME="body" ROWS="5" cols="50" ></TEXTAREA><br><br>
			Attach Files:<input type="file" name="file" size="50" multiple="multiple"/><br><br>
			
			<input class="button" type="button" value="Back" name="back" onclick="openPage('emailClient.jsp')"/>
			<input class="button" type="submit" value="Send" >
		</form>
	</fieldset>
	<script type="text/javascript">
		function openPage(pageURL) {
			window.location.href = pageURL;
		}
		var input = document.getElementById('filesToUpload');
		var list = document.getElementById('fileList');
		
		//empty list for now
		while (list.hasChildNodes()) {
			list.removeChild(ul.firstChild);
		}
		
		//for every file
		for (var x = 0; x < input.files.length; x++) {
			//add to list
			var li = document.createElement('li');
			li.innerHTML = 'File ' + (x + 1) + ':  ' + input.files[x].name;
			list.append(li);
		}
	</script>

</body>
</html>