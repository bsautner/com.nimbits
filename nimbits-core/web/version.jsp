<%@ page import="com.nimbits.client.enums.SettingType" %>


<html>
<head>


</head>
<body>
<p>Hello world</p>

<p>

    <% response.getWriter().println(SettingType.serverVersion.getDefaultValue());%>

</p>

</body>

</html>


