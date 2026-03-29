import kotlinx.datetime.LocalDateTime
import kotlin.properties.Delegates

enum class ObjectType(val printName: String, val printDollars: String) {
    BILL("bill", "amount due"), ACCOUNT("account", "balance")
}

class Account(var name: String, private var balance: Double) {
    override fun toString(): String {
        TODO("Return string formatted to use for a table")
    }

    fun editAccount(name: String = this.name, balance: Double = this.balance) {
        this.name = name
        this.balance = balance
    }
}

class Bill(
    var name: String,
    private var amount: Double,
    private var dueDate: LocalDateTime,
    private var account: Account
) {
    override fun toString(): String {
        TODO("return string formatted for table")
    }

    fun dateToString(): String {
        TODO("Return date as string formatted YYYY-MM-DD")
    }

    fun editBill(
        name: String = this.name,
        amount: Double = this.amount,
        dueDate: LocalDateTime = this.dueDate,
        account: Account = this.account
    ) {
        this.name = name
        this.amount = amount
        this.dueDate = dueDate
        this.account = account
    }
}

// Prompt for and return the name of the specified object type
fun namePrompt(type: ObjectType): String {
    var input: String
    // Loop until valid input
    while (true) {
        print("Input ${type.printName} name: ")
        input = readln()
        if (input.isEmpty()) {
            println("Entry cannot be blank. Try again.")
        } else {
            break
        }
    }
    return input
}

// Prompt for and return the amount/balance of the specified object type.
fun dollarPrompt(type: ObjectType): Double {
    var dollars: Double
    // Loop until valid input
    while (true) {
        print("Input ${type.printDollars}: ")
        try {
            dollars = readln().toDouble()
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