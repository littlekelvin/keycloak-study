package com.kelvin.adminclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.util.JsonSerialization;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class KeycloakHelperTest {
    private static final String REALM = "myRealm";
    private Keycloak keycloak;

    @BeforeEach
    void setUp() {
        keycloak = KeycloakHelper.getInstance();
    }

    @Test
    void getUsers() {
        List<UserRepresentation> users = keycloak.realm(REALM).users().list();
        for (UserRepresentation user : users) {
            System.out.println(user.getId() + ":" + user.getUsername());
        }
    }

    @Test
    void getUserByUserNameAndEmail() {
        List<UserRepresentation> users = keycloak.realm(REALM).users().search("kelvin"); // like username search
        for (UserRepresentation user : users) {
            System.out.println(user.getId() + ":" + user.getUsername());
        }
    }

    @Test
    void createUser() {
        UserRepresentation userPre = new UserRepresentation();
        userPre.setUsername("createdUser1");
        userPre.setFirstName("kelvin");
        userPre.setLastName("mai");
        List<CredentialRepresentation> credentials= new ArrayList<>();
        CredentialRepresentation crePassword = new CredentialRepresentation();
        crePassword.setType(CredentialRepresentation.PASSWORD);
        crePassword.setTemporary(false);
        crePassword.setValue("123");
        credentials.add(crePassword);
        userPre.setCredentials(credentials);
        userPre.setEnabled(true);
        Response response = keycloak.realm(REALM).users().create(userPre);
        if (Response.Status.CREATED .getStatusCode()== response.getStatus()){
            System.out.println("user created");
        } else if (Response.Status.CONFLICT.getStatusCode() == response.getStatus()) {
            System.out.println("user existed");
        } else {
            System.out.println("result:" + response.getStatus());
        }
    }

    @Test
    void updateUser() {
        UserResource userResource = keycloak.realm(REALM).users().get("b4b65f8f-5e52-45ca-8d6b-22ab2f64ba9d");
        UserRepresentation user = userResource.toRepresentation();
        System.out.println(user);
        user.setFirstName("Jack");
        user.setLastName("Yu");
        user.setEmail("1@oocl.com");
        userResource.update(user);
    }

    @Test
    void getSessions() {
        List<ClientRepresentation> clientRepresentations = keycloak.realm(REALM).clients().findAll();
        List<Map<String, String>> clientSessionStats = keycloak.realm(REALM).getClientSessionStats();
        ClientResource clientResource = keycloak.realm(REALM).clients().get("5ce94a77-39c8-4f2c-8b72-dcacab22ea43");
        List<UserSessionRepresentation> userSessions = clientResource.getUserSessions(0, Integer.MAX_VALUE);
        Map<String, Integer> applicationSessionCount = clientResource.getApplicationSessionCount();
        List<UserSessionRepresentation> offlineUserSessions = clientResource.getOfflineUserSessions(0, Integer.MAX_VALUE);
        Map<String, Long> offlineSessionCount = clientResource.getOfflineSessionCount();
        System.out.println(userSessions);
    }

    private static void handleClientErrorException(ClientErrorException e) {
        e.printStackTrace();
        Response response = e.getResponse();
        try {
            System.out.println("status: " + response.getStatus());
            System.out.println("reason: " + response.getStatusInfo().getReasonPhrase());
            Map error = JsonSerialization.readValue((ByteArrayInputStream) response.getEntity(), Map.class);
            System.out.println("error: " + error.get("error"));
            System.out.println("error_description: " + error.get("error_description"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
