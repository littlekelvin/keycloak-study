const KeycloakAdmin = require('keycloak-admin')

const adminClient = new KeycloakAdmin()
const initAdminClient = async () => {
    await adminClient.auth({
        username: 'admin',
        password: 'admin',
        grantType: 'password',
        clientId: 'admin-cli',
    })
    adminClient.setConfig({
        realmName: 'nodejs'
    })
}

