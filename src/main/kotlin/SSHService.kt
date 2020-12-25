import java.util.*

object SSHService {
    val config = Properties().apply {
        this["StrictHostKeyChecking"] = "no"
    }
}
