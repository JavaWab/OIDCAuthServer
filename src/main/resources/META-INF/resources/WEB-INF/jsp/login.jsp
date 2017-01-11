<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTDHTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" class="no-js">

<head>

    <meta charset="utf-8">
    <title>User Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- CSS -->
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=PT+Sans:400,700'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body>
<div class="page-container">
    <h1>用户登录</h1>
    <form method="post">
        <input type="text" name="username" class="username" placeholder="Username">
        <input type="password" name="password" class="password" placeholder="Password">
        <button class="ok" type="submit">登  录</button>
        <%
            Object msg = request.getAttribute("msg");
            Object error = request.getAttribute("error");
            if (msg != null) {
                out.print("<div><span>" + msg + "</span></div>");
            }
            if (error != null) {
                out.print("<div><span>" + error + "</span></div>");
            }
        %>
    </form>
</div>

<!-- Javascript -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

</body>

</html>

