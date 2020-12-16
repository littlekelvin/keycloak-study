import Vue from 'vue'
import App from './App.vue'
import Keycloak from 'keycloak-js'
import VueLogger from 'vuejs-logger'

Vue.use(VueLogger)

const initOptions = {
  url: 'http://localhost:8080/auth',
  realm: 'myRealm',
  clientId: 'vue-demo',
  onLoad: 'login-required'
}

const keycloak = Keycloak(initOptions)

keycloak.init({onLoad: initOptions.onLoad}).then(auth => {
  if (!auth) {
    window.location.reload();
  } else {
    Vue.$log.info('Authenticated')
    Vue.prototype.$keycloak = keycloak
    new Vue({
      el: '#app',
      render: h => h(App)
    })
  }

  // refresh token
  setInterval(() => {
    keycloak.updateToken(70).then((refreshed) => {
      if (refreshed) {
        Vue.$log.info('Token refreshed' + refreshed)
      } else {
        Vue.$log.warn(`Token not refreshed, valid for ${Math.round(keycloak.tokenParsed.exp + keycloak.timeSkew - new Date().getTime() / 1000)} seconds`)
      }
    }).catch(() => {
      Vue.$log.error('Failed to refresh token')
    });
  }, 6000)

}).catch(() => {
  Vue.$log.error("Authenticated Failed")
})
