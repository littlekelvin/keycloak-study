const express = require('express');
const path = require('path');
const bodyParser = require('body-parser')

const app = express();
app.use(bodyParser.json());

// app.get('/abc.html', keycloak.protect())
// static resources
app.use(express.static(path.join(__dirname, '/public')));

app.listen(3000, () => {
    console.log('Started at port 3000');
});

