package com.kelvin.jpastorage;

import com.kelvin.jpastorage.entity.UserEntity;
import com.kelvin.jpastorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JpaExampleUserStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider,
        CredentialInputUpdater,
        CredentialInputValidator,
        UserQueryProvider {

    private UserRepository userRepository;
    private ComponentModel model;
    private KeycloakSession session;

    public JpaExampleUserStorageProvider(UserRepository userRepository, ComponentModel model, KeycloakSession session) {
        this.userRepository = userRepository;
        this.model = model;
        this.session = session;
    }

    @Override
    public void close() {
        log.info("JpaExampleUserStorageProvider close");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info("getUserById# {}", id);
        String externalId = StorageId.externalId(id);
        UserEntity userEntity = userRepository.findById(externalId);
        return new UserAdapter(session, realm, model, userEntity, userRepository);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("getUserByUsername# {}", username);
        List<UserEntity> userEntitys = userRepository.findByUserName(username);
        if (null == userEntitys || userEntitys.isEmpty()) {
            log.warn("getUserByUsername# can not get user with username: {}", username);
            return null;
        }
        return new UserAdapter(session, realm, model, userEntitys.get(0), userRepository);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info("getUserByEmail# {}", email);
        List<UserEntity> userEntitys = userRepository.findByEmail(email);
        if (null == userEntitys || userEntitys.isEmpty()) {
            log.warn("getUserByEmail# can not get user with email: {}", email);
            return null;
        }
        return new UserAdapter(session, realm, model, userEntitys.get(0), userRepository);
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.info("addUser# {}", username);
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .build();
        userRepository.save(userEntity);
        return new UserAdapter(session, realm, model, userEntity, userRepository);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.info("removeUser# {}", user);
        String userId = StorageId.externalId(user.getId());
        return userRepository.deleteById(userId);
    }


    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("supportsCredentialType# {}", credentialType);
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("updateCredential# {}, {}", user, input);
        if (!(supportsCredentialType(input.getType())) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        String password = ((UserCredentialModel) input).getValue();
        getUserAdapter(user).setPassword(password);
        return userRepository.updateUserPassword(user.getUsername(), password);
    }

    public UserAdapter getUserAdapter(UserModel user) {
        UserAdapter adapter = null;
        if (user instanceof CachedUserModel) {
            adapter = (UserAdapter) ((CachedUserModel) user).getDelegateForUpdate();
        } else {
            adapter = (UserAdapter) user;
        }
        log.info("getUserAdapter# {}", adapter.getUserEntity());
        return adapter;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        log.info("disableCredentialType# {}", credentialType);
        if (!(supportsCredentialType(credentialType))) {
            return;
        }
        getUserAdapter(user).setPassword(null);
        userRepository.updateUserPassword(user.getUsername(), null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        log.info("getDisableableCredentialTypes# {}", user);
        if (getUserAdapter(user).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(PasswordCredentialModel.TYPE);
            return set;
        }
        return Collections.emptySet();
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info("isConfiguredFor# {} {}", user, credentialType);
        return supportsCredentialType(credentialType) && getUserAdapter(user).getPassword() != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("isConfiguredFor# {} {}", user, credentialInput);
        if (!(supportsCredentialType(credentialInput.getType())) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }
        String inputPass = ((UserCredentialModel) credentialInput).getValue();
        String password = getUserAdapter(user).getPassword();
        return null != password && password.equals(inputPass);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        log.info("getUsersCount# {}", realm.getName());
        return userRepository.count();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.info("getUsers# {}", realm.getName());
        return getUsers(realm, -1, -1);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        log.info("getUsers# {}", realm.getName());
        List<UserEntity> userEntities = userRepository.findAllUsers(firstResult, maxResults);
        return userEntities.stream()
                .map(userEntity -> new UserAdapter(session, realm, model, userEntity, userRepository))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return searchForUser(search, realm, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info("searchForUser# {}", search);
        List<UserEntity> userEntities = userRepository.findUserByUserNameOrEmailLike(search, firstResult, maxResults);
        return userEntities.stream()
                .map(userEntity -> new UserAdapter(session, realm, model, userEntity, userRepository))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return searchForUser(params, realm, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        log.info("searchForUser map# {}", params);
        String usernameSearchStr = params.get("username");
        if (null == usernameSearchStr) {
            return getUsers(realm);
        }
        return searchForUser(usernameSearchStr, realm, firstResult, maxResults);
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
