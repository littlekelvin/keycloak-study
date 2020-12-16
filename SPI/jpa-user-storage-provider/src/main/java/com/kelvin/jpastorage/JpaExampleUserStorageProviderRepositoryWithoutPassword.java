package com.kelvin.jpastorage;

import com.kelvin.jpastorage.base.DBPersistenceUnitInfo;
import com.kelvin.jpastorage.base.UserStorageDBMetadataConfig;
import com.kelvin.jpastorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kelvin.jpastorage.constants.DBConnectionConstant.*;

@Slf4j
public class JpaExampleUserStorageProviderRepositoryWithoutPassword implements UserStorageProviderFactory<JpaExampleUserStorageWithoutPasswordProvider> {

    public static final String PERSISTEN_UNIT = "MYSQL_DS";
    private EntityManagerFactory entityManagerFactory;
    protected static final List<ProviderConfigProperty> configMetadata;

    static {
        configMetadata = UserStorageDBMetadataConfig.initConfigMetadata();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        entityManagerFactory = null;
        onCreate(session, realm, newModel);
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        MultivaluedHashMap<String, String> configMap = config.getConfig();
        if(StringUtils.isBlank(configMap.getFirst(DB_CONNECTION_NAME_KEY))) {
            throw new ComponentValidationException("Connection name empty.");
        }
        if(StringUtils.isBlank(configMap.getFirst(DB_HOST_KEY))) {
            throw new ComponentValidationException("Database host empty.");
        }
        if(!StringUtils.isNumeric(configMap.getFirst(DB_PORT_KEY))) {
            throw new ComponentValidationException("Invalid port. (Empty or NaN)");
        }
        if(StringUtils.isBlank(configMap.getFirst(DB_DATABASE_KEY))) {
            throw new ComponentValidationException("Database name empty.");
        }
        if(StringUtils.isBlank(configMap.getFirst(DB_USERNAME_KEY))) {
            throw new ComponentValidationException("Database username empty.");
        }
        if(StringUtils.isBlank(configMap.getFirst(DB_PASSWORD_KEY))) {
            throw new ComponentValidationException("Database password empty.");
        }
    }

    @Override
    public JpaExampleUserStorageWithoutPasswordProvider create(KeycloakSession session, ComponentModel model) {
        log.info("create JpaExampleUserStorageProvider");
        String connectionName = model.getConfig().getFirst(DB_CONNECTION_NAME_KEY);
        if (null == entityManagerFactory) {
            entityManagerFactory = initEntityManagerFactory(model.getConfig());
        }

        UserRepository userRepository = new UserRepository(entityManagerFactory.createEntityManager());
        return new JpaExampleUserStorageWithoutPasswordProvider(userRepository, model, session);
    }

    private EntityManagerFactory initEntityManagerFactory(MultivaluedHashMap<String, String> config) {
        Map<String, String> dbConfigMap = new HashMap<>();
        dbConfigMap.put("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
        dbConfigMap.put("hibernate.connection.url",
                String.format("jdbc:mysql://%s:%s/%s",
                        config.getFirst(DB_HOST_KEY),
                        config.getFirst(DB_PORT_KEY),
                        config.getFirst(DB_DATABASE_KEY)));
        dbConfigMap.put("hibernate.connection.username", config.getFirst(DB_USERNAME_KEY));
        dbConfigMap.put("hibernate.connection.password", config.getFirst(DB_PASSWORD_KEY));
        dbConfigMap.put("hibernate.show-sql", "true");
        dbConfigMap.put("hibernate.hbm2ddl.auto", "none");
        dbConfigMap.put("hibernate.connection.autocommit", "true");
        return new HibernatePersistenceProvider().createContainerEntityManagerFactory(new DBPersistenceUnitInfo(PERSISTEN_UNIT), dbConfigMap);
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
