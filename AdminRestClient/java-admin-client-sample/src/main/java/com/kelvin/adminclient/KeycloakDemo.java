package com.kelvin.adminclient;

import org.keycloak.representations.idm.RealmRepresentation;

import java.util.List;

public class KeycloakDemo {
    public static void main(String[] args) {
        List<RealmRepresentation> realms = KeycloakHelper.getInstance().realms().findAll();
        for (RealmRepresentation realm : realms) {
            System.out.println(realm.getId());
        }
    }
}
