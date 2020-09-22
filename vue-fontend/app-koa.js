const Keycloak = require('@anthinkingcoder/keycloak-koa-connect');
const koa = require('koa');
const session = require('koa-session');
const Router = require('koa-router');
const path = require('path');
const static = require('koa-static')
const mount = require('koa-mount')
const keycloakConfig = require('./keycloak-config')
const jwt = require('jsonwebtoken')

const app = new koa();
const router = new Router();

const MemoryStore = require('./memory-store')
const store = new MemoryStore()
app.use(session({
    key: 'koa:sess',
    maxAge: 86400000,
    renew: false,
    store: store,
    signed: false
}, app));

const keycloak = new Keycloak({
    store: true
}, keycloakConfig);

const middlewares = keycloak.middleware({
    logout: '/logout',
    admin: '/'
});
middlewares.forEach(middleware => {
    app.use(middleware);
})

// router.get('/sec', keycloak.protect(), function (ctx) {
//     ctx.response.body = '<h5>Index public</h5>';
// });
// router.get('/hello', function (ctx) {
//     ctx.response.body = '<h5>Index</h5>';
// });
const keyCall = keycloak.protect()
router.get('*', async (ctx, next) => {
    let originalUrl = ctx.originalUrl
    if (originalUrl.indexOf('abc') > -1 || originalUrl === '/') {
        await next()
    } else {
        return keyCall(ctx, next)
    }
}, async (ctx, next) => {
    const keycloakToken = ctx.session['keycloak-token']
    let userId = 'test'
    if (keycloakToken) {
        userId = jwt.decode(JSON.parse(ctx.session['keycloak-token']).access_token).preferred_username
    }
    ctx.cookies.set('user_id', userId)
    await next()
})

router.get('/index1.html', async function (ctx, next) {
    await next()
});

router.get('/work.html', async function (ctx, next) {
    await next()
});

app.use(router.routes()).use(router.allowedMethods());
app.use(mount('/auth', static(path.join(__dirname, 'views'))))

app.listen(3002, () => {
    console.log('server started at port: ', 3002)
});


