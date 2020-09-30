package com.kelvin.jpastorage;

import com.kelvin.jpastorage.entity.UserEntity;
import com.kelvin.jpastorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private UserEntity userEntity;
    private String keycloakId;
    private UserRepository userRepository;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, UserEntity userEntity, UserRepository userRepository) {
        super(session, realm, storageProviderModel);
        this.userEntity = userEntity;
        this.keycloakId = StorageId.keycloakId(storageProviderModel, userEntity.getId());
        this.userRepository = userRepository;
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        userEntity.setUsername(username);
    }

    public String getPassword() {
        return userEntity.getPassword();
    }

    public void setPassword(String password) {
        userEntity.setPassword(password);
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getEmail() {
        log.info("getEmail# {}");
        return userEntity.getEmail();
    }

    @Override
    public void setEmail(String email) {
        log.info("setEmail# {}", email);
        userEntity.setEmail(email);
        userRepository.updateUser(userEntity);
    }

    @Override
    public String getFirstName() {
        return userEntity.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        log.info("setFirstName# {}", firstName);
        userEntity.setFirstName(firstName);
        userRepository.updateUser(userEntity);
    }

    @Override
    public String getLastName() {
        return userEntity.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        log.info("setLastName# {}", lastName);
        userEntity.setLastName(lastName);
        userRepository.updateUser(userEntity);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        log.info("setSingleAttribute# {}:{}", name, value);
        if ("phone".equals(name)) {
            userEntity.setPhone(value);
            userRepository.updateUser(userEntity);
        } else if ("email".equals(name)) {
            userEntity.setEmail(value);
            userRepository.updateUser(userEntity);
        } else if ("firstName".equals(name)) {
            userEntity.setFirstName(value);
            userRepository.updateUser(userEntity);
        } else if ("lastName".equals(name)) {
            userEntity.setLastName(value);
            userRepository.updateUser(userEntity);
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        log.info("removeAttribute# {}", name);
        if ("phone".equals(name)) {
            userEntity.setPhone(null);
            userRepository.updateUser(userEntity);
        } else if ("email".equals(name)) {
            userEntity.setEmail(null);
            userRepository.updateUser(userEntity);
        } else if ("firstName".equals(name)) {
            userEntity.setFirstName(null);
            userRepository.updateUser(userEntity);
        } else if ("lastName".equals(name)) {
            userEntity.setLastName(null);
            userRepository.updateUser(userEntity);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        log.info("setAttribute# {}:{}", name, values);
        if (name.equals("phone")) {
            userEntity.setPhone(values.get(0));
            userRepository.updateUser(userEntity);
        } else if ("email".equals(name)) {
            userEntity.setEmail(values.get(0));
            userRepository.updateUser(userEntity);
        } else if ("firstName".equals(name)) {
            userEntity.setFirstName(values.get(0));
            userRepository.updateUser(userEntity);
        } else if ("lastName".equals(name)) {
            userEntity.setLastName(values.get(0));
            userRepository.updateUser(userEntity);
        } else {
            super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        log.info("getFirstAttribute# {}", name);
        if (name.equals("phone")) {
            return userEntity.getPhone();
        } else if (name.equals("email")) {
            return userEntity.getEmail();
        } else if (name.equals("firstName")) {
            return userEntity.getFirstName();
        } else if (name.equals("lastName")) {
            return userEntity.getLastName();
        } else {
            return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attributes = super.getAttributes();
        log.info("getAttributes# {}", attributes);
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attributes);
        all.put("phone", userEntity.getPhone() == null ? Collections.emptyList() : List.of(userEntity.getPhone()));
        all.put("email", userEntity.getEmail() == null ? Collections.emptyList() : List.of(userEntity.getEmail()));
        all.put("firstName", userEntity.getFirstName() == null ? Collections.emptyList() : List.of(userEntity.getFirstName()));
        all.put("lastName", userEntity.getLastName() == null ? Collections.emptyList() : List.of(userEntity.getLastName()));
        log.info("getAttributes all# {}", all);
        return all;
    }

    @Override
    public List<String> getAttribute(String name) {
        log.info("getAttribute# {}", name);
        if ("phone".equals(name)) {
            List<String> phone = new LinkedList<>();
            phone.add(userEntity.getPhone());
            return phone;
        } else {
            return super.getAttribute(name);
        }
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }
}
