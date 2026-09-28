//
//package com.example.emaillist.controller;
//
//import com.example.emaillist.dao.UserDAO;
//import com.example.emaillist.model.User;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//@WebServlet("/emailList")
//public class EmailListServlet extends HttpServlet {
//
//    private final UserDAO userDAO =
//            new UserDAO();
//
//    @Override
//    protected void doGet(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws ServletException, IOException {
//
//        request.getRequestDispatcher("/index.jsp")
//                .forward(request, response);
//    }
//
//    @Override
//    protected void doPost(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws ServletException, IOException {
//
//        request.setCharacterEncoding("UTF-8");
//
//        // 1. Lấy dữ liệu từ form
//        String email =
//                request.getParameter("email");
//
//        String firstName =
//                request.getParameter("firstName");
//
//        String lastName =
//                request.getParameter("lastName");
//
//        // 2. Kiểm tra dữ liệu rỗng
//        if (email == null
//                || firstName == null
//                || lastName == null) {
//
//            response.sendRedirect(
//                    request.getContextPath()
//                            + "/emailList"
//            );
//
//            return;
//        }
//
//        email = email.trim();
//        firstName = firstName.trim();
//        lastName = lastName.trim();
//
//        // 3. Tạo đối tượng User
//        User user = new User(
//                email,
//                firstName,
//                lastName
//        );
//
//        request.setAttribute("user", user);
//
//        if (email.isEmpty()
//                || firstName.isEmpty()
//                || lastName.isEmpty()) {
//
//            request.setAttribute(
//                    "message",
//                    "Please fill in all fields."
//            );
//
//            request.getRequestDispatcher("/index.jsp")
//                    .forward(request, response);
//
//            return;
//        }
//
//        try {
//
//            // 4. Kiểm tra email đã tồn tại chưa
//            if (userDAO.emailExists(email)) {
//
//                request.setAttribute(
//                        "message",
//                        "This email address already exists. "
//                                + "Please enter another email address."
//                );
//
//                request.getRequestDispatcher("/index.jsp")
//                        .forward(request, response);
//
//            } else {
//
//                // 5. Thêm người dùng vào database
//                userDAO.insert(user);
//
//                // Chuyển đến trang thành công
//                request.getRequestDispatcher("/thanks.jsp")
//                        .forward(request, response);
//            }
//
//        } catch (SQLException e) {
//
//            // Trường hợp email bị trùng khi INSERT
//            if (e.getErrorCode() == 1062) {
//
//                request.setAttribute(
//                        "message",
//                        "This email address already exists. "
//                                + "Please enter another email address."
//                );
//
//                request.getRequestDispatcher("/index.jsp")
//                        .forward(request, response);
//
//            } else {
//
//                throw new ServletException(
//                        "Database error",
//                        e
//                );
//            }
//        }
//    }
//}



package com.example.emaillist.controller;

import com.example.emaillist.dao.UserDAO;
import com.example.emaillist.model.User;
import com.example.emaillist.util.MailUtil;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    private final UserDAO userDAO =
            new UserDAO();

    // =====================================
    // GET: Hiển thị form đăng ký
    // =====================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher("/index.jsp")
                .forward(request, response);

    }

    // =====================================
    // POST: Xử lý đăng ký
    // =====================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. Lấy dữ liệu từ form

        String email =
                request.getParameter("email");

        String firstName =
                request.getParameter("firstName");

        String lastName =
                request.getParameter("lastName");

        // 2. Kiểm tra dữ liệu rỗng

        if (email == null ||
                firstName == null ||
                lastName == null ||
                email.isBlank() ||
                firstName.isBlank() ||
                lastName.isBlank()) {

            request.setAttribute(
                    "message",
                    "Vui lòng nhập đầy đủ thông tin."
            );

            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);

            return;
        }

        // Chuẩn hóa dữ liệu

        email = email.trim();
        firstName = firstName.trim();
        lastName = lastName.trim();

        try {

            // 3. Kiểm tra email đã tồn tại

            if (userDAO.emailExists(email)) {

                request.setAttribute(
                        "message",
                        "Email này đã được đăng ký."
                );

                request.getRequestDispatcher("/index.jsp")
                        .forward(request, response);

                return;
            }

            // 4. Tạo đối tượng User

            User user = new User(
                    email,
                    firstName,
                    lastName
            );

            // 5. Lưu User bằng JPA

            userDAO.addUser(user);

            // Gửi email cảm ơn sau khi lưu thành công.
            // Lỗi SMTP không làm mất đăng ký đã lưu trong database.
            try {
                MailUtil.sendWelcomeEmail(email, firstName);
            } catch (IOException e) {
                log("Đăng ký đã được lưu nhưng không gửi được email tới " + email, e);
            }

            // 6. Truyền User sang JSP

            request.setAttribute(
                    "user",
                    user
            );

            // 7. Hiển thị trang thành công

            request.getRequestDispatcher("/thanks.jsp")
                    .forward(request, response);

        } catch (PersistenceException e) {

            throw new ServletException(
                    "Database error",
                    e
            );

        }
    }
}
