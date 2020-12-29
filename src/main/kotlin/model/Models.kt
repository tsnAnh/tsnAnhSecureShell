package model

import javafx.beans.property.SimpleObjectProperty

data class Session(val terminalHistory: String, val sessionStarted: Long, val username: String, val hostname: String)

class SessionModel(terminalHistory: String, sessionStarted: Long, username: String, hostname: String) {
    val terminalHistory = SimpleObjectProperty(terminalHistory)
    val sessionStarted = SimpleObjectProperty(sessionStarted)
    val username = SimpleObjectProperty(username)
    val hostname = SimpleObjectProperty(hostname)
}
