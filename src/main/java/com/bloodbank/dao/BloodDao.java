package com.bloodbank.dao;

import com.bloodbank.model.*;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class BloodDao {
    public List<BloodInventory> inventory(Session session) {
        return session.createQuery("from BloodInventory order by bloodGroup", BloodInventory.class).list();
    }
    public BloodInventory findInventory(Session session, String group) {
        Query<BloodInventory> query = session.createQuery("from BloodInventory where bloodGroup = :group", BloodInventory.class);
        query.setParameter("group", group);
        return query.uniqueResult();
    }
    public BloodInventory findInventoryForUpdate(Session session, String group) {
        Query<BloodInventory> query = session.createQuery("from BloodInventory where bloodGroup = :group", BloodInventory.class);
        query.setParameter("group", group);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.uniqueResult();
    }
    public List<BloodRequest> pendingRequests(Session session) {
        return session.createQuery("from BloodRequest where status = 'PENDING' order by createdAt", BloodRequest.class).list();
    }
    public List<BloodRequest> requestsByUser(Session session, User user) {
        Query<BloodRequest> query = session.createQuery("from BloodRequest where requester = :user order by createdAt desc", BloodRequest.class);
        query.setParameter("user", user);
        return query.list();
    }
}
