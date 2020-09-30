package com.kelvin.jpastorage.base;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

import static com.kelvin.jpastorage.constants.DBConnectionConstant.*;

public class UserStorageDBMetadataConfig {

    public static List<ProviderConfigProperty> initConfigMetadata() {
        return ProviderConfigurationBuilder.create()
                // Connection Name
                .property().name(DB_CONNECTION_NAME_KEY)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Connection Name")
                .defaultValue("")
                .helpText("Name of the connection, can be chosen individually. Enables connection sharing between providers if the same name is provided. Overrides currently saved connection properties.")
                .add()

                // Connection Host
                .property().name(DB_HOST_KEY)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Host")
                .defaultValue("localhost")
                .helpText("Host of the connection")
                .add()

                // DB Port
                .property().name(DB_PORT_KEY)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Port")
                .defaultValue("3306")
                .add()

                // Connection Database
                .property().name(DB_DATABASE_KEY)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Name")
                .add()

                // DB Username
                .property().name(DB_USERNAME_KEY)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Username")
                .defaultValue("user")
                .add()

                // DB Password
                .property().name(DB_PASSWORD_KEY)
                .type(ProviderConfigProperty.PASSWORD)
                .label("Database Password")
                .defaultValue("PASSWORD")
                .add()
                .build();
    }
}
