package com.kelvin.jpastorage.repository;

import com.kelvin.jpastorage.entity.UserEntity;
import com.kelvin.jpastorage.utils.PersistenceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(PersistenceUtil.getEntityManager());
    }

    @Test
    void saveTest() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username("kelvin2")
                .password("123")
                .email("12@oocl.com")
                .firstName("KANGJIE")
                .lastName("MAI")
                .phone("10086")
                .build();
        userRepository.save(user);
    }

    @Test
    void testDelete() {
        boolean result = userRepository.deleteById("12d62d22-8ba0-478c-9e2e-1217e77e3b43");
        assertThat(result).isTrue();
    }

    @Test
    void testUpdatePassword() {
        boolean result = userRepository.updateUserPassword("kelvin", "123456");
        assertThat(result).isTrue();
    }

    @Test
    void testFindById() {
        UserEntity userEntity = userRepository.findById("48121e03-e5aa-4fda-a690-ffe83128a49d");
        assertThat(userEntity.getUsername()).isEqualTo("kelvin");
    }
}