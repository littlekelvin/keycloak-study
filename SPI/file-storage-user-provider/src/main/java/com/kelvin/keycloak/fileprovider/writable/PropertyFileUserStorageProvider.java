package com.kelvin.keycloak.fileprovider.writable;

import org.jboss.logging.Logger;
import org.keycloak.common.util.EnvUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import sun.security.krb5.Realm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PropertyFileUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator,
        CredentialInputUpdater, UserRegistrationProvider, UserQueryProvider {
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
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info("#getUserById#" + id + realm.getName());
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
        if (!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) credentialInput;
        String password = properties.getProperty(user.getUsername());
        log.info("#isValid# pass-" + password + ", input pass-" + credentialInput.getChallengeResponse());
        if (password == null || UNSET_PASSWORD.equals(password)) {
            return false;
        }
        return password.equals(cred.getChallengeResponse());
    }

    // Update password
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("updateCredential-");
        if (!(input instanceof UserCredentialModel) || !PasswordCredentialModel.TYPE.equals(input.getType())) {
            return false;
        }
        synchronized (properties) {
            properties.setProperty(user.getUsername(), ((UserCredentialModel) input).getValue());
            save();
        }
        return true;
    }

    // disable password
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!PasswordCredentialModel.TYPE.equals(credentialType)) {
            return;
        }
        synchronized (properties) {
            properties.setProperty(user.getUsername(), UNSET_PASSWORD);
            save();
        }
    }

    private static Set<String> disabledTypes = new HashSet<>();

    static {
        disabledTypes.add(PasswordCredentialModel.TYPE);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return disabledTypes;
    }

    public static final String UNSET_PASSWORD = "#$!-UNSET-PASSWORD";

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.info("addUser:" + username);
        synchronized (properties) {
            properties.setProperty(username, UNSET_PASSWORD);
            save();
        }
        return createAdapter(realm, username);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.info("removeUser:" + user.getUsername());
        synchronized (properties) {
            if (properties.remove(user.getUsername()) == null) {
                return false;
            }
            save();
            return true;
        }
    }

    private void save() {
        String path = this.model.getConfig().getFirst("path");
        path = EnvUtil.replace(path);
        try {
            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, "");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return properties.size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return getUsers(realm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        List<UserModel> userModels = new ArrayList<>();
        int i = 0;
        for (Object obj : properties.keySet()) {
            if (i++ < firstResult) {
                continue;
            }
            UserModel user = getUserByUsername((String) obj, realm);
            userModels.add(user);
            if (userModels.size() >= maxResults) {
                break;
            }
        }
        return userModels;
    }

    // search by username and email
    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return searchForUser(search, realm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info("searchForUser:" + search);
        List<UserModel> userModels = new ArrayList<>();
        int i = 0;
        for (Object obj : properties.keySet()) {
            String username = (String) obj;
            if (i++ < firstResult) {
                continue;
            }
            if (!username.contains(search)) {
                continue;
            }
            UserModel user = getUserByUsername(username, realm);
            userModels.add(user);
            if (userModels.size() >= maxResults) {
                break;
            }
        }
        return userModels;
    }

    // search for a user based on firstName, lastName, username, email etc.
    // for this property case, we only have username to store
    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return searchForUser(params, realm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        String usernameSearchStr = params.get("username");
        log.info("searchForUser map:" + usernameSearchStr);
        if (null == usernameSearchStr) {
            return getAllUsers(realm);
        }
        return searchForUser(usernameSearchStr, realm, firstResult, maxResults);
    }

    private List<UserModel> getAllUsers(RealmModel realm) {
        List<UserModel> userModels = new ArrayList<>();
        for (Object obj : properties.keySet()) {
            userModels.add(this.getUserByUsername((String)obj, realm));
        }
        return userModels;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return Collections.EMPTY_LIST;
    }
}
