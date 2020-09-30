package com.kelvin.jpastorage.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceUtil {
    private PersistenceUtil() {

    }

    private static EntityManagerFactory entityManagerFactory;

    private static final String MYSQL_DS_UNIT = "mysqlDS";

    static {
        entityManagerFactory = Persistence.createEntityManagerFactory(MYSQL_DS_UNIT);
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
