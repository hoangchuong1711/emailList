
package com.example.emaillist.dao;

import com.example.emaillist.model.User;
import com.example.emaillist.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UserDAO {

    // =====================================
    // 1. Tìm người dùng theo email
    // =====================================

    public User selectUser(String email) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            String jpql =
                    "SELECT u FROM User u " +
                            "WHERE u.email = :email";

            TypedQuery<User> query =
                    em.createQuery(
                            jpql,
                            User.class
                    );

            query.setParameter(
                    "email",
                    email
            );

            List<User> users =
                    query.getResultList();

            if (users.isEmpty()) {
                return null;
            }

            return users.get(0);

        } finally {

            em.close();

        }
    }

    // =====================================
    // 2. Kiểm tra email tồn tại
    // =====================================

    public boolean emailExists(String email) {

        User user = selectUser(email);

        return user != null;

    }

    // =====================================
    // 3. Thêm người dùng
    // =====================================

    public void addUser(User user) {

        EntityManager em =
                JPAUtil.getEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            em.persist(user);

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction.isActive()) {

                transaction.rollback();

            }

            throw e;

        } finally {

            em.close();

        }
    }
}