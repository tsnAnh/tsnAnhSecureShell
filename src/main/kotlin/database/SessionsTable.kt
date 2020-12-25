package database

import org.jetbrains.exposed.dao.id.UUIDTable

object SessionsTable : UUIDTable(name = "sessions") {
    val terminalHistory = varchar(name = "terminal_history", length = Int.MAX_VALUE)
    val sessionStarted = long(name = "session_started")
    val username = varchar(name = "username", length = 255)
    val hostname = varchar(name = "hostname", length = 255)
}
