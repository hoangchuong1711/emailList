<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Email List</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>
    <h1>Join our email list</h1>

    <p>To join our email list, enter your name and email address below.</p>

    <!-- Hiển thị thông báo lỗi -->
    <c:if test="${not empty message}">
        <p class="message"><c:out value="${message}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/emailList" method="post">
        <div class="form-row">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email"
                   value="<c:out value='${user.email}'/>" required>
        </div>

        <div class="form-row">
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName"
                   value="<c:out value='${user.firstName}'/>" required>
        </div>

        <div class="form-row">
            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName"
                   value="<c:out value='${user.lastName}'/>" required>
        </div>

        <button type="submit">Join Now</button>
    </form>
</body>
</html>