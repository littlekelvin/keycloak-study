const express = require('express');
const path = require('path');
const bodyParser = require('body-parser')
const session = require('express-session')
const KeycloakConnect = require('keycloak-connect')

// keycloak
const memoryStore = new session.MemoryStore();
const keycloak = new KeycloakConnect({store: memoryStore})

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
app.get('/service/public', function (req, res) {
    res.json({message: 'public'});
});
app.get('/service/secured', keycloak.protect('realm:USER'), function (req, res) {
    res.json({message: 'secured'});
});
app.get('/abc.html', keycloak.protect())
app.get('*', keycloak.protect())
// static resources
app.use(express.static(path.join(__dirname, '/views')));

app.use('*', function (req, res) {
    res.send('Not found!');
});

app.listen(3001, () => {
    console.log('Started at port 3001');
});

