//package com.kelvin.keycloak.fileprovider.readonly;
//
//import org.jboss.logging.Logger;
//import org.keycloak.Config;
//import org.keycloak.component.ComponentModel;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.storage.UserStorageProviderFactory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//public class PropertyFileUserStorageProviderFactory implements UserStorageProviderFactory<PropertyFileUserStorageProvider> {
//    public static final String PROVIDER_NAME = "readonly-property-file";
//
//    protected Properties properties = new Properties();
//    private static final Logger logger = Logger.getLogger(PropertyFileUserStorageProviderFactory.class);
//
//
//    @Override
//    public PropertyFileUserStorageProvider create(KeycloakSession session, ComponentModel model) {
//        logger.info("#create#");
//        return new PropertyFileUserStorageProvider(session, properties, model);
//    }
//
//    @Override
//    public String getId() {
//        return PROVIDER_NAME;
//    }
//
//    @Override
//    public void init(Config.Scope config) {
//        logger.info("init user profiles!");
//        InputStream in = getClass().getClassLoader().getResourceAsStream("users.properties");
//        if (in == null) {
//            logger.warn("Could not find users.properties in classpath\"");
//        } else {
//            try {
//                properties.load(in);
//            } catch (IOException e) {
//                logger.error("Failed to load users.properties file", e);
//            }
//        }
//    }
//}
