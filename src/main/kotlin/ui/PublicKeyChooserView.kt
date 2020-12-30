package ui

import database.SessionsTable
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TextField
import model.SessionModel
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*
import javax.swing.JOptionPane

class PublicKeyChooserView : View("tsnAnh's Secure Shell: Login") {
    private lateinit var tfPassword: TextField
    private lateinit var tfUsername: TextField
    private lateinit var tfHostName: TextField
    private lateinit var tfPublicKeyPath: TextField
    private val listSession = transaction {
        SessionsTable.selectAll().map {
            SessionModel(
                it[SessionsTable.terminalHistory].take(10),
                it[SessionsTable.sessionStarted],
                it[SessionsTable.username],
                it[SessionsTable.hostname]
            )
        }
    }
    override val root: Parent = hbox {
        tableview(listSession.asObservable()) {
            column("History", SessionModel::terminalHistory)
            column("Session Started", SessionModel::sessionStarted)
            column("Username", SessionModel::username)
            column("Hostname", SessionModel::hostname)
        }
        vbox {
            paddingAll = 8
            spacing = 8.0
            vbox {
                label("Username")
                tfUsername = textfield()
                label("Hostname")
                tfHostName = textfield()
                label("Password")
                tfPassword = textfield()
            }
            hbox {
                spacing = 4.0
                label("Public key")
                tfPublicKeyPath = textfield {
                    isEditable = false
                    prefColumnCount = 20
                }
                button("Choose") {
                    action(::showFileChooser)
                }
            }
            hbox {
                alignment = Pos.CENTER_RIGHT
                button("OK") {
                    action {
                        try {
                            val started = Calendar.getInstance().timeInMillis
                            if (tfHostName.text.isNotBlank() && tfUsername.text.isNotBlank() && tfPublicKeyPath.text.isNotBlank()) {
                                transaction {
                                    SessionsTable.insertAndGetId {
                                        it[terminalHistory] = ""
                                        it[sessionStarted] = started
                                        it[username] = tfUsername.text
                                        it[hostname] = tfHostName.text
                                    }
                                }
                                find<ConsoleView>(
                                    mapOf(
                                        ConsoleView::path to tfPublicKeyPath.text,
                                        ConsoleView::hostname to tfHostName.text,
                                        ConsoleView::username to tfUsername.text,
                                        ConsoleView::started to started,
                                        ConsoleView::password to tfPassword.text
                                    )
                                ).openWindow(owner = null)
                                close()
                            } else {
                                JOptionPane.showMessageDialog(
                                    null,
                                    "One of three fields must not blank!",
                                    "Error",
                                    JOptionPane.OK_OPTION
                                )
                            }
                        } catch (e: Exception) {
                            println("Error: ${e.localizedMessage}")
                        }
                    }
                }
            }
        }
    }

    private fun showFileChooser() {
        val files = chooseFile(
            "Choose a public key",
            mode = FileChooserMode.Single,
            filters = arrayOf()
        )
        if (files.isEmpty()) return
        tfPublicKeyPath.text = files.first().path
    }
}
