package com.kelvin.adminclient;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakHelper {
    private static Keycloak keycloak;

    public static Keycloak getInstance() {
        if (keycloak == null) {
            synchronized (KeycloakHelper.class) {
                keycloak = initialKeycloakClient();
            }
        }
        return keycloak;
    }

    private static Keycloak initialKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080/auth")
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
}
