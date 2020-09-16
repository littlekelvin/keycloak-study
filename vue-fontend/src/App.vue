<template>
  <div id="app">
    <img src="./assets/logo.png">
    <h1>{{ msg }}</h1>
    <p><span>current user:</span> {{username}}</p>
    <p><span>current roles:</span> {{roles}}</p>
    <p><span>indexMsg:</span> {{indexMsg}}</p>
    <p><span>userMsg:</span> {{userMsg}}</p>
    <p><span>adminMsg:</span> {{adminMsg}}</p>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'app',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App',
      userMsg: '',
      adminMsg: '',
      indexMsg: '',
    }
  },
  computed: {
    username() {
      console.log('$keycloak', this.$keycloak)
      return this.$keycloak.idTokenParsed.preferred_username
    },
    roles() {
      return this.$keycloak.realmAccess.roles
    }
  },
  created() {
    this.commonUserGet().then(response => {
      this.userMsg = response.data
    }).catch((err) => {
      console.log('commonUserGet', err)
    })

    this.adminUserGet().then(response => {
      this.adminMsg = response.data
    }).catch((err) => {
      console.log('adminUserGet', err)
    })

    this.indexGet().then(response => {
      this.indexMsg = response.data
    }).catch((err) => {
      console.log('indexGet', err)
    })
  },
  methods: {
    commonUserGet() {
      return axios({
        method: 'get',
        url: '/backend/users/user',
        headers: {'Authorization': 'Bearer ' + this.$keycloak.token}
      })
    },
    adminUserGet() {
      return axios({
        method: 'get',
        url: '/backend/admins/admin',
        headers: {'Authorization': 'Bearer ' + this.$keycloak.token}
      })
    },
    indexGet() {
      return axios({
        method: 'get',
        url: '/backend/'
      })
    }
  }
}
</script>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
