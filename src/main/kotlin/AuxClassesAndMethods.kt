import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

enum class ObjectType(val printName: String, val printDollars: String) {
    BILL("bill", "amount due"), ACCOUNT("account", "balance")
}

@Serializable
class Account(var name: String, var balance: Double) {
    override fun toString(): String {
        return "\"$name\" with balance $${"%3.2f".format(balance)}."
    }
}

@Serializable
class Bill(
    var name: String,
    var amount: Double,
    var dueDate: LocalDateTime,
    var account: Account
) {
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
}

// Prompt for and return the name of the specified object type
// return null if entry is blank
fun namePrompt(type: ObjectType, isUpdate: Boolean = false): String? {
    var input: String
    // Loop until valid input
    while (true) {
        print("Input ${type.printName} name: ")
        input = readln()
        if (input.isEmpty() && isUpdate) return null
        else if (input.isEmpty()) {
            println("Entry cannot be blank. Try again.")
        } else {
            break
        }
    }
    return input
}

// Prompt for and return the amount/balance of the specified object type.
// return null if entry is blank
fun dollarPrompt(type: ObjectType, isUpdate: Boolean = false): Double? {
    var dollars: Double
    // Loop until valid input
    while (true) {
        print("Input ${type.printDollars}: ")
        val input = readln()
        try {
            if (input.isEmpty() && isUpdate) return null
            dollars = input.toDouble()
            break
        } catch (e: NumberFormatException) {
            println("Invalid input. Try again. Enter in format #.## without a dollar sign.")
            LOGGER.warn("Invalid input to dollarPrompt", e)
        }
    }
    return dollars
}

// Confirmation dialog, to confirm changes to bill/account.
// Requires toString output for given object.
fun confirmationPrompt(stringObj: String, type: ObjectType): Boolean {
    var confirmation: Boolean? = null
    // Loop until valid input
    while (confirmation == null) {
        println(stringObj)
        print("Is this ${type.printName} correct? Y/N: ")
        when (readln().uppercase()) {
            "Y" -> confirmation = true
            "N" -> confirmation = false
            else -> println("Invalid input. Try again.")
        }
    }
    return confirmation
}

// Prompt for and return LocalDateTime. Does not require ObjectType as it is only used for Bill objects.
fun datePrompt(isUpdate: Boolean = false): LocalDateTime? {
    var dueDate: LocalDateTime
    while (true) {
        print("Please enter the due date. Format: YYYY-MM-DD: ")
        val input = readln()
        if (input.isEmpty() && isUpdate) {
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