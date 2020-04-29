<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page pageEncoding="UTF-8"%>
<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="messages"/>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 Error</title>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <style>
        .centered {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div style="text-align: center" class="centered">
                <h1>Ooops!</h1>
                <h2>404 not found</h2>
                <p>Sorry, an error has occurred, Requested page not found!</p>
                <button onclick="location.href='${pageContext.request.contextPath}/app/index'" type="button" class="btn btn-outline-secondary">Go no main page</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>