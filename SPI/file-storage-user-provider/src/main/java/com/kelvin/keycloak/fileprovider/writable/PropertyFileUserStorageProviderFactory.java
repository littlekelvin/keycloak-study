package com.kelvin.keycloak.fileprovider.writable;

import org.jboss.logging.Logger;
import org.keycloak.common.util.EnvUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class PropertyFileUserStorageProviderFactory implements UserStorageProviderFactory<PropertyFileUserStorageProvider> {
    public static final String PROVIDER_NAME = "readonly-property-file";
    private static final Logger logger = Logger.getLogger(PropertyFileUserStorageProviderFactory.class);
    protected static List<ProviderConfigProperty> configMetadata;

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                .property()
                .name("path")
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Path")
                .defaultValue("${jboss.server.config.dir}/example-users.properties")
                .helpText("File path to properties file")
                .add()
                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        logger.info("getConfigProperties!");
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        logger.info("validateConfiguration!");
        String fp = config.getConfig().getFirst("path");
        if (null == fp) {
            throw new ComponentValidationException("User property file does not exists");
        }
        fp = EnvUtil.replace(fp);
        logger.info("validateConfiguration! path " + fp);
        File file = new File(fp);
        if (!file.exists()) {
            throw new ComponentValidationException("User property file does not exists");
        }
    }

    @Override
    public PropertyFileUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        logger.info("create user profiles!");
        String path = model.getConfig().getFirst("path");
        path = EnvUtil.replace(path);
        logger.info("create user profiles! path: " + path);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            logger.error("Failed to load users.properties file", e);
        }
        return new PropertyFileUserStorageProvider(session, properties, model);
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
