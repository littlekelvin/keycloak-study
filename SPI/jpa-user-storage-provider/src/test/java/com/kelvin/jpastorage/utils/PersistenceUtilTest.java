package com.kelvin.jpastorage.utils;

import com.kelvin.jpastorage.entity.UserEntity;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.UUID;

class PersistenceUtilTest {
    @Test
    void shouldSaveEntity_whenSave_givenUserEntity() {
        EntityManager em = PersistenceUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username("kelvin")
                .password("123")
                .email("12@oocl.com")
                .firstName("KANGJIE")
                .lastName("MAI")
                .phone("10086")
                .build();
        em.persist(user);
        tx.commit();
        em.close();
    }
}