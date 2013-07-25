<%@ page import="com.google.apphosting.api.ApiProxy" %>
<%--
  Created by IntelliJ IDEA.
  User: mattstep
  Date: 2/3/13
  Time: 11:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Starter Application Page</title>
</head>
<body>
    <p>Welcome to <%= ApiProxy.getCurrentEnvironment().getAppId() %>, There isn't anything running here yet.</p>
</body>
</html>