package com.bloodbank.dao;

import com.bloodbank.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDao {
    public User findByUsername(Session session, String username) {
        Query<User> query = session.createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public boolean hasAdmin(Session session) {
        return session.createQuery("select count(u.id) from User u where u.role = 'ADMIN'", Long.class)
                .getSingleResult() > 0;
    }
}
