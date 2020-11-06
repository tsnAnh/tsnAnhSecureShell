package ui

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TextField
import tornadofx.*
import javax.swing.JOptionPane

class PublicKeyChooserView : View("Choose a public key") {
    private lateinit var tfUsername: TextField
    private lateinit var tfHostName: TextField
    private lateinit var tfPublicKeyPath: TextField
    override val root: Parent = vbox {
        paddingAll = 8
        spacing = 8.0
        vbox {
            label("Username")
            tfUsername = textfield()
            label("Hostname")
            tfHostName = textfield()
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
                    if (tfHostName.text.isNotBlank() && tfUsername.text.isNotBlank() && tfPublicKeyPath.text.isNotBlank()) {
                        find<ConsoleView>(
                            mapOf(
                                ConsoleView::path to tfPublicKeyPath.text,
                                ConsoleView::hostname to tfHostName.text,
                                ConsoleView::username to tfUsername.text,
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
