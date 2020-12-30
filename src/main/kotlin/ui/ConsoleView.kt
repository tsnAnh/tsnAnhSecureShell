package ui

import SSHService
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import database.SessionsTable
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.util.*
import kotlin.system.exitProcess

class ConsoleView : View("tsnAnh's Secure Shell: Console") {
    private lateinit var userCombobox: ComboBox<String>
    private lateinit var tfCommand: TextField
    val path: String by param()
    val username: String by param()
    val hostname: String by param()
    val password: String by param()
    val started: Long by param()
    private lateinit var txtConsole: TextArea
    private val jsch = JSch().apply {
        addIdentity(path)
    }
    private var session = jsch.getSession(username, hostname).apply {
        setConfig(SSHService.config)
        setPassword(password)
    }

    private val user = SimpleStringProperty(username)
    init {
        session.connect()
    }

    override val root: Parent = vbox {
        txtConsole = textarea()
        hbox {
            useMaxWidth = true
            userCombobox = combobox(user, listOf(username, "sudo"))
            tfCommand = textfield {
                useMaxWidth = true
                setOnKeyPressed { evt ->
                    if (evt.code == KeyCode.ENTER) {
                        if (tfCommand.text.toLowerCase(Locale.ROOT) in listOf("exit")) {
                            exitProcess(0)
                        }
                        val channel = getSession().openChannel("exec")
                        with(channel as ChannelExec) {
                            setCommand(if (user.get() == "sudo") "sudo $text" else text)
                            inputStream = null
                            setErrStream(System.err)
                        }

                        val bfr = BufferedReader(InputStreamReader(channel.inputStream as PipedInputStream))

                        channel.connect(5000)
                        bfr.forEachLine { s ->
                            txtConsole.appendText("$s\n")
                        }
                        transaction {
                            SessionsTable.update({ SessionsTable.sessionStarted eq started }) {
                                it[terminalHistory] = txtConsole.text
                            }
                        }
                        tfCommand.text = ""
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun getSession(): Session {
        try {
            val testChannel = session.openChannel("exec") as ChannelExec
            testChannel.setCommand("true")
            testChannel.connect()
            testChannel.disconnect()
        } catch (t: Throwable) {
            session = jsch.getSession(username, hostname)
            session.setConfig(SSHService.config)
            session.setPassword("")
            session.connect()
        }
        return session
    }
}
