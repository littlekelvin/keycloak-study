package com.kelvin.keycloak.fileprovider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyFileUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator, CredentialInputUpdater {
    private static final Logger log = Logger.getLogger(PropertyFileUserStorageProvider.class);

    protected KeycloakSession session;
    protected Properties properties;
    protected ComponentModel model;
    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public PropertyFileUserStorageProvider(KeycloakSession session, Properties properties, ComponentModel model) {
        this.session = session;
        this.properties = properties;
        this.model = model;
    }

    @Override
    public void close() {}

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info("#getUserById#"+id+ realm.getName());
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("#getUserByUsername#： " + username + realm.getName());
        UserModel adapter = loadedUsers.get(username);
        if (null == adapter) {
            String password = properties.getProperty(username);
            if (null != password) {
                adapter = createAdapter(realm, username);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
    }

    private UserModel createAdapter(RealmModel realm, String username) {
        return new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        String password = properties.getProperty(user.getUsername());
        return credentialType.equals(PasswordCredentialModel.TYPE) && password != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("#isValid#");
        if (!supportsCredentialType(credentialInput.getType())) {
            return false;
        }
        String password = properties.getProperty(user.getUsername());
        log.info("#isValid# pass-"+password +", input pass-" + credentialInput.getChallengeResponse());
        if (password == null) {
            return false;
        }
        return password.equals(credentialInput.getChallengeResponse());
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (PasswordCredentialModel.TYPE.equals(input.getType())) {
            throw new ReadOnlyException("user is read only for this update");
        }
        return false;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return null;
    }
}
