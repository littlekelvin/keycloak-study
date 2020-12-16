package com.kelvin.keycloak.fileprovider.readonly;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

import java.util.List;
import java.util.Set;

public class TestProvider implements UserFederatedStorageProvider {
    @Override
    public List<String> getStoredUsers(RealmModel realm, int first, int max) {
        return null;
    }

    @Override
    public int getStoredUsersCount(RealmModel realm) {
        return 0;
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Override
    public void preRemove(RealmModel realm, ClientModel client) {

    }

    @Override
    public void preRemove(ProtocolMapperModel protocolMapper) {

    }

    @Override
    public void preRemove(ClientScopeModel clientScope) {

    }

    @Override
    public void preRemove(RealmModel realm, UserModel user) {

    }

    @Override
    public void preRemove(RealmModel realm, ComponentModel model) {

    }

    @Override
    public void close() {

    }

    @Override
    public void setSingleAttribute(RealmModel realm, String userId, String name, String value) {

    }

    @Override
    public void setAttribute(RealmModel realm, String userId, String name, List<String> values) {

    }

    @Override
    public void removeAttribute(RealmModel realm, String userId, String name) {

    }

    @Override
    public MultivaluedHashMap<String, String> getAttributes(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public List<String> getUsersByUserAttribute(RealmModel realm, String name, String value) {
        return null;
    }

    @Override
    public String getUserByFederatedIdentity(FederatedIdentityModel socialLink, RealmModel realm) {
        return null;
    }

    @Override
    public void addFederatedIdentity(RealmModel realm, String userId, FederatedIdentityModel socialLink) {

    }

    @Override
    public boolean removeFederatedIdentity(RealmModel realm, String userId, String socialProvider) {
        return false;
    }

    @Override
    public void preRemove(RealmModel realm, IdentityProviderModel provider) {

    }

    @Override
    public void updateFederatedIdentity(RealmModel realm, String userId, FederatedIdentityModel federatedIdentityModel) {

    }

    @Override
    public Set<FederatedIdentityModel> getFederatedIdentities(String userId, RealmModel realm) {
        return null;
    }

    @Override
    public FederatedIdentityModel getFederatedIdentity(String userId, String socialProvider, RealmModel realm) {
        return null;
    }

    @Override
    public void addConsent(RealmModel realm, String userId, UserConsentModel consent) {

    }

    @Override
    public UserConsentModel getConsentByClient(RealmModel realm, String userId, String clientInternalId) {
        return null;
    }

    @Override
    public List<UserConsentModel> getConsents(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void updateConsent(RealmModel realm, String userId, UserConsentModel consent) {

    }

    @Override
    public boolean revokeConsentForClient(RealmModel realm, String userId, String clientInternalId) {
        return false;
    }

    @Override
    public void updateCredential(RealmModel realm, String userId, CredentialModel cred) {

    }

    @Override
    public CredentialModel createCredential(RealmModel realm, String userId, CredentialModel cred) {
        return null;
    }

    @Override
    public boolean removeStoredCredential(RealmModel realm, String userId, String id) {
        return false;
    }

    @Override
    public CredentialModel getStoredCredentialById(RealmModel realm, String userId, String id) {
        return null;
    }

    @Override
    public List<CredentialModel> getStoredCredentials(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public List<CredentialModel> getStoredCredentialsByType(RealmModel realm, String userId, String type) {
        return null;
    }

    @Override
    public CredentialModel getStoredCredentialByNameAndType(RealmModel realm, String userId, String name, String type) {
        return null;
    }

    @Override
    public Set<GroupModel> getGroups(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void joinGroup(RealmModel realm, String userId, GroupModel group) {

    }

    @Override
    public void leaveGroup(RealmModel realm, String userId, GroupModel group) {

    }

    @Override
    public List<String> getMembership(RealmModel realm, GroupModel group, int firstResult, int max) {
        return null;
    }

    @Override
    public void setNotBeforeForUser(RealmModel realm, String userId, int notBefore) {

    }

    @Override
    public int getNotBeforeOfUser(RealmModel realm, String userId) {
        return 0;
    }

    @Override
    public Set<String> getRequiredActions(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void addRequiredAction(RealmModel realm, String userId, String action) {

    }

    @Override
    public void removeRequiredAction(RealmModel realm, String userId, String action) {

    }

    @Override
    public void grantRole(RealmModel realm, String userId, RoleModel role) {

    }

    @Override
    public Set<RoleModel> getRoleMappings(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void deleteRoleMapping(RealmModel realm, String userId, RoleModel role) {

    }
}
