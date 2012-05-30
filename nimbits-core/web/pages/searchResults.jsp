<%@ page import="com.nimbits.client.constants.Const" %><%


    response.setContentType(Const.CONTENT_TYPE_PLAIN);
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Access-Control-Allow-Origin", "*");
%>


${TEXT}