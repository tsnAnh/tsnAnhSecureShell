package ui

import SSHService
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Paint
import tornadofx.View
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox
import java.nio.charset.Charset
import java.util.*

class ConsoleView : View() {
    private lateinit var tfCommand: TextField
    val path: String by param()
    val username: String by param()
    val hostname: String by param()
    init {
        println("Path: $path, Username: $username, Hostname: $hostname")
    }
    private lateinit var txtConsole: TextArea
    private val jsch = JSch().apply {
        addIdentity(path)
    }
    private var session = jsch.getSession(username, hostname).apply {
        setConfig(SSHService.config)
        setPassword("")
    }

    override val root: Parent = vbox {
        txtConsole = textarea {
            background = Background(
                BackgroundFill(
                    Paint.valueOf("#000000"),
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        }
        tfCommand = textfield {
            setOnKeyPressed { evt ->
                if (evt.code == KeyCode.ENTER) {
                    val channel = getSession().openChannel("exec")
                    (channel as ChannelExec).setCommand(text)
                    channel.inputStream = null
                    channel.setErrStream(System.err)

                    val inputStream = channel.inputStream
                    channel.connect(5000)
                    while (!false) {
                        txtConsole.appendText("${inputStream.readBytes().toString(Charset.defaultCharset())}\n")
                        if (channel.isClosed) {
                            txtConsole.appendText("Done\n")
                            break
                        }
                    }
                }
            }
        }
    }

    private fun getSession(): Session {
        try {
            val testChannel = session.openChannel("exec") as ChannelExec
            testChannel.run {
                setCommand("true")
                connect()
                disconnect()
            }
        } catch (e: Exception) {
            session = jsch.getSession(username, hostname, 22).apply {
                setPassword("")
                setConfig(SSHService.config)
            }
            session.connect()
        }
        return session
    }
}
