package com.kelvin.jpastorage.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public abstract class WriteTransactionBlock<T> implements Transaction<T> {
    private EntityManager entityManager;

    @Override
    public T execute() {
        this.init();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        T t = this.run();
        tx.commit();
        return t;
    }

    @Override
    public T execute(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        T t = this.run();
        tx.commit();
        return t;
    }

    private void init() {
        entityManager = PersistenceUtil.getEntityManager();
    }

    public abstract T run();
}
