package ui

import SSHService
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.jetbrains.exposed.dao.Entity
import tornadofx.View
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.util.*
import kotlin.system.exitProcess


class ConsoleView : View() {
    private lateinit var tfCommand: TextField
    val path: String by param()
    val username: String by param()
    val hostname: String by param()
    val idRow: Entity<UUID> by param()
    private lateinit var txtConsole: TextArea
    private val jsch = JSch().apply {
        addIdentity(path)
    }
    private var session = jsch.getSession(username, hostname).apply {
        setConfig(SSHService.config)
        setPassword("")
    }

    init {
        session.connect()
    }

    override val root: Parent = vbox {
        txtConsole = textarea {
        }
        tfCommand = textfield {
            setOnKeyPressed { evt ->
                if (evt.code == KeyCode.ENTER) {
                    if (tfCommand.text.toLowerCase(Locale.ROOT) in listOf("exit")) {
                        exitProcess(0)
                    }
                    val channel = getSession().openChannel("exec")
                    with(channel as ChannelExec) {
                        setCommand(text)
                        inputStream = null
                        setErrStream(System.err)
                    }

                    val bfr = BufferedReader(InputStreamReader(channel.inputStream as PipedInputStream))

                    channel.connect(5000)
//                    while (!false) {
//                        val text = inputStream.readBytes().toString(Charset.defaultCharset())
//                        transaction {
//                            SessionsTable.update({ SessionsTable.id eq idRow.id }) {
//                                with(SqlExpressionBuilder) {
//                                    it.update(terminalHistory, terminalHistory + text)
//                                }
//                            }
//                        }
//                        txtConsole.appendText(text)
//                        if (channel.isClosed) {
//                            txtConsole.appendText("Done\n")
//                            break
//                        }
//                    }
                    bfr.forEachLine {
                        txtConsole.appendText("$it\n")
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
