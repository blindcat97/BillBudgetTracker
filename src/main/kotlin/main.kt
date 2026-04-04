import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

val LOGGER: Logger = LoggerFactory.getLogger("root")
var initialized = false

fun main() {
    if (!initialized) {
        // Introductory message
        println(
            """
            Bill tracker by Adrienn Varn
        """.trimIndent()
        )
        println("\n")
        // Database check
        Database.importHandler()
        // TODO: fix that it says "file not found" during init
        initialized = true
    }

    while (true) {
        println()
        println("Please enter the number of your desired command:")
        println("1. View bills")
        println("2. Manage accounts")
        println("3. Exit program")
        when (readln()) {
            "1" -> manageBills()
            "2" -> manageAccounts()
            "3" -> {
                println("Thanks for using this budget tracker!")
                Database.exportHandler()
                exitProcess(0)
            }
            else -> println("Invalid command, please try again.")
        }
    }
}

fun manageBills() {
    val oType = OType.BILL
    while (true) {
        println()
        if (Database.accountSet.isNotEmpty() && Database.billSet.isNotEmpty()) {
            Database.accountSet.forEach { acct ->
                Database.billSet.filter { it.account == acct }
                    .forEach { println(it) }
            }
            println()
        }
        print("What would you like to do? add, update, delete, back: ")
        when (readln().lowercase()) {
            "add" -> {
                if (Database.accountSet.isEmpty()) {
                    println("You have to create an account to add bills to first.")
                } else {
                    val newBill = createObject(oType) as Bill
                    if (confirmationPrompt(newBill.toString(), oType)) Database.billSet.add(newBill)
                    else println("Operation cancelled.")
                }
            }
            "update" -> {
                if (Database.billSet.isEmpty()) {
                    println("There are no bills yet! Add some first!")
                } else {
                    Database.existingBillPrompt().updateBill()
                }
            }
            "delete" -> {
                if (Database.billSet.isEmpty()) {
                    println("There are no bills yet! Add some first!")
                } else {
                    Database.existingBillPrompt().deleteBill()
                }
            }
            "back" -> return
            else -> println("Invalid input, please try again.")
        }
    }

}

fun manageAccounts() {
    val oType = OType.ACCOUNT
    while (true) {
        println()
        if (Database.accountSet.isNotEmpty()) {
            Database.accountSet.forEach { println(it) }
            println()
        }
        print("What would you like to do? add, update, delete, back: ")
        when (readln().lowercase()) {
            "add" -> {
                val newAccount = createObject(oType) as Account
                if (confirmationPrompt(newAccount.toString(), oType)) Database.accountSet.add(newAccount)
                else println("Operation cancelled.")
            }
            "update" -> {
                if (Database.accountSet.isEmpty()) {
                    println("There are no accounts to update! Add some first!")
                } else {
                    var accountToUpdate = Database.existingAccountPrompt()
                    while (accountToUpdate == null) {
                        println("Name cannot be empty. Try again.")
                        accountToUpdate = Database.existingAccountPrompt()
                    }
                    accountToUpdate.updateAccount()
                }
            }
            "delete" -> {
                if (Database.accountSet.isEmpty()) {
                    println("There are no accounts to delete! Add some first!")
                } else {
                    var accountToUpdate = Database.existingAccountPrompt()
                    while (accountToUpdate == null) {
                        println("Name cannot be empty. Try again.")
                        accountToUpdate = Database.existingAccountPrompt()
                    }
                    accountToUpdate.deleteAccount()
                }
            }
            "back" -> return
            else -> println("Invalid input, please try again.")
        }
    }
}