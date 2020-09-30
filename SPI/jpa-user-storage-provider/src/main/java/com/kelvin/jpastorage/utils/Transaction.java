package com.kelvin.jpastorage.utils;

import javax.persistence.EntityManager;

public interface Transaction<T> {
    T execute();
    T execute(EntityManager entityManager);
}
