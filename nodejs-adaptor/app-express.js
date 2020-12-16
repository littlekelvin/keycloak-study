const express = require('express');
const path = require('path');
const bodyParser = require('body-parser')
const session = require('express-session')
const KeycloakConnect = require('keycloak-connect')
const keycloakConfig = require('./keycloak-config')
const jwt = require('jsonwebtoken')
const  router = express.Router();

// keycloak
const memoryStore = new session.MemoryStore();
const keycloak = new KeycloakConnect({store: memoryStore}, keycloakConfig)

const app = express();
app.use(bodyParser.json());

app.use(session({
    secret: 'some secret',
    resave: false,
    saveUninitialized: true,
    store: memoryStore
}));

app.use(keycloak.middleware({
    logout: '/logout',
    admin: '/'
}));

// api settings
router.get('/service/public', (req, res) => {
    res.json({message: 'public'});
});
router.get('/service/secured', keycloak.protect('realm:USER'), function (req, res) {
    res.json({message: 'secured'});
});

const keycloakProtect = keycloak.protect()
router.get('*', (req, resp, next) => {
    const originalUrl = req.originalUrl
    if (originalUrl.indexOf('abc') > -1 || originalUrl === '/' || originalUrl.indexOf('favicon.ico') > -1) {
        return next()
    }
    return keycloakProtect(req, resp, next)
}, (req, resp, next) => {
    const keycloakToken = req.session['keycloak-token']
    let userId = 'test'
    if (keycloakToken) {
        userId = jwt.decode(JSON.parse(keycloakToken).access_token).preferred_username
    }
    const cookiesOption = {
        maxAge: 1000 * 60 * 60 * 24,
        httpOnly: false
    }
    resp.cookie('user_id', userId, cookiesOption)
    return next()
})

app.use(router)
// static resources
app.use(express.static(path.join(__dirname, '/views')));


app.listen(3000, () => {
    console.log('Started at port 3001');
});

