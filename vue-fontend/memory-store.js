function MemoryStore() {
    this.sessions = Object.create(null);
}

MemoryStore.prototype.get = function (sessionId) {
    var sess = this.sessions[sessionId];
    if (!sess) {
        return
    }
    sess = JSON.parse(sess);
    if (sess.expire && sess.expire <= Date.now()) {
        delete this.sessions[sessionId];
        return
    }
    return sess
}

MemoryStore.prototype.set = function (sessionId, session, maxAge) {
    if (maxAge > 0) {
        session.expire = Date.now() + maxAge;
    }
    this.sessions[sessionId] = JSON.stringify(session);
}

MemoryStore.prototype.destroy = function (sessionId) {
    delete this.sessions[sessionId];
}


module.exports = MemoryStore;
