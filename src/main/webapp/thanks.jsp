<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thank You</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>
    <h1>Thanks for joining our email list!</h1>

    <p>Here is the information that you entered:</p>

    <p class="info">
        <b>Email:</b> <c:out value="${user.email}"/>
    </p>

    <p class="info">
        <b>First Name:</b> <c:out value="${user.firstName}"/>
    </p>

    <p class="info">
        <b>Last Name:</b> <c:out value="${user.lastName}"/>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/emailList">
            Back to registration
        </a>
    </p>
</body>
</html>