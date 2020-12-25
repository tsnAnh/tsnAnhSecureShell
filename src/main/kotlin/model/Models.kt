package model

data class Session(val terminalHistory: String, val sessionStarted: Long, val username: String, val hostname: String)
