<%@ page import="java.util.Enumeration" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTDHTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" class="no-js">

<head>

    <meta charset="utf-8">
    <title>User Approval</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- CSS -->
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=PT+Sans:400,700'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body>
<div class="page-container">
    <h1>访问授权确认</h1>
    <form action="${pageContext.request.contextPath}/oauth/authorize" method="post">
        <span>您同意[${redirect_uri}]访问您如下信息吗？</span>
        <div align="left" style="padding-left: 50px;">
            <c:forEach var="scope" items="${scopes}">
                <input type="checkbox" name="scope.${scope}" value="true" checked><font color="black">${scope}</font><br>
            </c:forEach>
        </div>
        <input name="user_oauth_approval" value="true" type="hidden">
        <button class="ok" type="submit">同意</button>
    </form>
    <form id="denialForm" name="denialForm" action="${pageContext.request.contextPath}/oauth/authorize" method="post">
        <input name="user_oauth_approval" value="false" type="hidden">
        <label>
            <button class="no" type="submit">拒绝</button>
        </label>
    </form>
</div>

<!-- Javascript -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

</body>

</html>

