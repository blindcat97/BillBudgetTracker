import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.serialization.Serializable
import kotlin.Boolean

enum class OType(val printName: String, val printDollars: String) {
    BILL("bill", "amount due"), ACCOUNT("account", "balance")
}

fun nameInput(oType: OType): String? {
    print("Input ${oType.printName} name: ")
    val input: String = readln()
    if (input.isEmpty()) return null
    return input
}
fun dollarInput(oType: OType): Double? {
    var dollars: Double
    // Loop until valid input
    while (true) {
        print("Input ${oType.printDollars}: ")
        val input = readln()
        try {
            if (input.isEmpty()) return null
            dollars = input.toDouble()
            break
        } catch (e: NumberFormatException) {
            println("Invalid input. Try again. Enter in format #.## without a dollar sign.")
            LOGGER.warn("Invalid input to dollarPrompt", e)
        }
    }
    return dollars
}
fun dateInput(): LocalDateTime? {
    var dueDate: LocalDateTime
    while (true) {
        print("Please enter the due date. Format: YYYY-MM-DD: ")
        val input = readln()
        if (input.isEmpty()) {
            return null
        }
        try {
            val dateStrings = input.split("-")
            val dateInts = dateStrings.map { it.toInt() }
            dueDate = LocalDateTime(
                year = dateInts[0],
                month = dateInts[1],
                day = dateInts[2],
                hour = 0,
                minute = 0
            )
            break
        } catch (e: IllegalArgumentException) {
            println("Date inputs were not valid. Did you enter a number wrong?")
        } catch (e: NumberFormatException) {
            println("Date improperly formatted. Please try again.")
        } catch (e: Exception) {
            println("Unidentified error. Logged.")
            LOGGER.error("Unidentified error in date conversion. ", e.stackTrace)
        }
    }
    return dueDate
}
fun confirmationPrompt(objString: String, oType: OType, isDeletion: Boolean = false): Boolean {
    var confirmation: Boolean? = null
    // Loop until valid input
    while (confirmation == null) {
        println(objString)
        if (isDeletion) print("Is this ${oType.printName} you want to delete? Y/N: ")
        else print("Is this ${oType.printName} correct? Y/N: ")
        when (readln().uppercase()) {
            "Y" -> confirmation = true
            "N" -> confirmation = false
            else -> println("Invalid input. Try again.")
        }
    }
    return confirmation
}

fun createObject(oType: OType): Any {
    var name: String? = null
    var dollars: Double? = null
    var dueDate: LocalDateTime? = null
    println("No value can be left blank.")
    while (name == null) {
        name = nameInput(oType)
    }
    while (dollars == null) {
        dollars = dollarInput(oType)
    }
    var account: Account? = null
    if (oType == OType.BILL) {
        while (dueDate == null) {
            dueDate = dateInput()
        }
        account = Database.existingAccountPrompt()
        while (account == null) {
            println("Value cannot be blank.")
            account = Database.existingAccountPrompt()
        }
    }
    return when (oType) {
        OType.ACCOUNT -> Account(name, dollars)
        OType.BILL -> Bill(name, dollars, dueDate!!, account!!)
    }
}

@Serializable
class Account(var name: String, var balance: Double) {
    val oType = OType.ACCOUNT
    override fun toString(): String {
        return "\"$name\" with balance $${"%3.2f".format(balance)}."
    }

    fun updateAccount() {
        println(this)
        println("Leave inputs blank to keep existing value.")
        val newName = nameInput(oType) ?: name
        val newBalance = dollarInput(oType) ?: balance
        if (confirmationPrompt(Account(newName, newBalance).toString(), oType)) {
            this.apply {
                name = newName
                balance = newBalance
            }
        } else {
            println("Operation cancelled.")
        }
    }

    fun deleteAccount() {
        if (confirmationPrompt(this.toString(), oType, true)) Database.removeAccount(this)
        else println("Operation cancelled.")
    }
}

@Serializable
class Bill(
    var name: String,
    var amount: Double,
    var dueDate: LocalDateTime,
    var account: Account
) {
    val oType = OType.BILL

    // Returns String in format: "NAME     $###.##   YYYY-MM-DD"
    override fun toString(): String {
        return "%-10s $%3.2f %14s %10s".format(name, amount, dateToString(), account.name)
    }

    // Returns date as String in YYYY-MM-DD format.
    fun dateToString(): String {
        return "${dueDate.year}-" +
                dueDate.month.number.toString().format("$02s") + "-" +
                dueDate.day.toString().format("%02s")
    }

    fun updateBill() {
        println(this)
        println("Leave inputs blank to keep existing values.")
        val newName = nameInput(oType) ?: name
        val newAmount = dollarInput(oType) ?: amount
        val newDate = dateInput() ?: dueDate
        val newAccount = Database.existingAccountPrompt() ?: account
        if (confirmationPrompt(Bill(newName, newAmount, newDate, newAccount).toString(), oType)) {
            this.apply {
                name = newName
                amount = newAmount
                dueDate = newDate
                account = newAccount
            }
        } else println("Operation cancelled.")
    }

    fun deleteBill() {
        if (confirmationPrompt(this.toString(), oType, true)) Database.removeBill(this)
        else println("Operation cancelled.")
    }

}