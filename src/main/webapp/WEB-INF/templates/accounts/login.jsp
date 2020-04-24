<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml" xmlns:v-on="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link rel="stylesheet" th:href="@{/css/login.css}">

</head>
<body>
<div id="app" class="login-form">
    <form action="${pageContext.request.contextPath}/app/login" method="post">
        <h2 class="text-center"  th:text="#{string.reg.title.login}"></h2>
        <div class="form-group">
            <input name="username" id="username" type="email" class="form-control" th:placeholder="#{string.reg.placeholder.email}"
                   required="required">
        </div>
        <div class="form-group">
            <input type="password" name="password" id="password" class="form-control" th:placeholder="#{string.reg.placeholder.password}"
                   required="required">
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block" th:text="#{string.reg.title.login}"></button>
        </div>
        <div class="clearfix">
            <label class="pull-left checkbox-inline"><input type="checkbox" name="remember-me" th:text="#{string.login.remember.me}"></label>
            <a href="#" class="pull-right" th:text="#{string.forgot.password}"></a>
        </div>
        <div style="display: flex; align-items: center; justify-content: center">
            <p class="error" th:if="${param.error}" th:text="#{string.login.invalid.username.password}"></p>
            <p id="logout" class="FadeIn fifth" th:if="${param.logout}" th:text="#{string.login.user.been.logged.out}"></p>
        </div>
    </form>
    <p class="text-center"><a th:href="@{/accounts/registration}" th:text="#{string.login.button.create.account}"></a></p>
    <ul style="display: flex; align-items: center; justify-content: center" id="lang">
        <li><a class="underlineHover" href="?lang=en" th:text="EN"></a></li>
        <li><a class="underlineHover" href="?lang=ua" th:text="UA"></a></li>
    </ul>
</div>

</body>
</html>