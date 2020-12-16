package com.kelvin.jpastorage;

import com.kelvin.jpastorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Slf4j
public class JpaExampleUserStorageProviderRepository2 implements UserStorageProviderFactory<JpaExampleUserStorageProvider> {
    private static final String MYSQL_DS_UNIT = "mysqlDS";

    @Override
    public JpaExampleUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        log.info("create JpaExampleUserStorageProvider");
        UserRepository userRepository = new UserRepository(getEntityManager());
        return new JpaExampleUserStorageProvider(userRepository, model, session);
    }

    public static EntityManager getEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(MYSQL_DS_UNIT);
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public String getId() {
        return "jpa-example-provider";
    }

    @Override
    public void close() {
        log.info("JpaExampleUserStorageProviderRepository close!");
    }
}
