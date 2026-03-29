import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("root")
var initMessageShown = false

fun main() {
    // Introductory message
    println("""
        Hello welcome to my program
        I hope you like it
    """.trimIndent())
    println("\n")
    initMessageShown = true
}