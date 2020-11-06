import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.util.*

object SSHService {
    val config = Properties().apply {
        this["StrictHostKeyChecking"] = "no"
        this["PreferredAuthentications"] = ""
    }
}
