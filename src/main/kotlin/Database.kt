import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class Database {
    companion object {
        var accountSet = mutableSetOf<Account>()
        var billSet = mutableSetOf<Bill>()
        var selectedAccount: Account? = null

        //******** File Handlers ********//
        fun importHandler(filename: String = "default.json") {
            try {
                val dbPair =
                    Json.decodeFromString<Pair<MutableSet<Account>, MutableSet<Bill>>>(File(filename).readText())
                accountSet = dbPair.first
                billSet = dbPair.second
                println("Load successful.")
            } catch (_: FileNotFoundException) {
                println("File not found: $filename")
            }
        }

        fun exportHandler(filename: String = "default.json") {
            try {
                val dbPair = Pair(accountSet, billSet)
                File(filename).writeText(Json.encodeToString(dbPair))
                println("Save successful.")
            } catch (_: FileNotFoundException) {
                println("File not found: $filename")
            }
        }

        //********* Retrieve Items **********//
        fun accountByName(name: String): Account? {
            return accountSet.firstOrNull { it.name == name }
        }

        fun existingAccountPrompt(): Account {
            var testAccount: Account
            while (true) {
                accountSet.forEach { println(it) }
                if (selectedAccount != null) {
                    println("Active account: ${selectedAccount!!.name}. Leave prompt blank to use this account.")
                }
                val name = namePrompt(ObjectType.ACCOUNT, isUpdate = true)
                if (selectedAccount != null && name == null) {
                    return selectedAccount!!
                }
                if (accountByName(name!!) == null) {
                    println("No account by that name exists. Please try again.")
                } else {
                    testAccount = accountByName(name)!!
                    break
                }
            }
            return testAccount
        }

        fun billByName(name: String): Bill? {
            return billSet.firstOrNull { it.name == name }
        }

        fun existingBillPrompt(): Bill {
            var testBill: Bill
            while (true) {
                val name = namePrompt(ObjectType.BILL)
                if (billByName(name!!) == null) {
                    println("No bill by that name exists. Please try again.")
                } else {
                    testBill = billByName(name)!!
                    break
                }
            }
            return testBill
        }

        //******** Manage Accounts ********//
        fun addAccount() {
            val name = namePrompt(ObjectType.ACCOUNT)
            val balance = dollarPrompt(ObjectType.ACCOUNT)
            // Asserting that name and balance are non-null because isUpdate = false
            val newAccount = Account(name!!, balance!!)
            if (confirmationPrompt(newAccount.toString(), ObjectType.ACCOUNT)) {
                accountSet.add(newAccount)
                LOGGER.debug("Account added: {}", newAccount)
                println("New account added successfully.")
                selectedAccount = newAccount
                println("${newAccount.name} set as active account.")
            } else {
                println("Returning to menu.")
            }
        }

        fun updateAccount() {
            var oldAccount = existingAccountPrompt()
            println(oldAccount)
            println("Press enter at any prompt to keep existing value.")
            var name = namePrompt(ObjectType.ACCOUNT, isUpdate = true)
            var balance = dollarPrompt(ObjectType.ACCOUNT, isUpdate = true)
            if (name == null) {
                name = oldAccount.name
            }
            if (balance == null) {
                balance = oldAccount.balance
            }
            val newAccount = Account(name, balance)
            if (confirmationPrompt(newAccount.toString(), ObjectType.ACCOUNT)) {
                LOGGER.debug("Account updated: {} -> {}", oldAccount, newAccount)
                oldAccount = newAccount
                println("Account updated successfully.")
            } else {
                println("Returning to menu.")
            }
        }

        fun deleteAccount() {
            val account = existingAccountPrompt()
            if (confirmationPrompt(account.toString(), ObjectType.ACCOUNT)) {
                accountSet.remove(account)
                LOGGER.debug("Account deleted: {}", account)
                println("Account deleted successfully.")
                if (selectedAccount == account) {
                    selectedAccount = null
                }
            } else {
                println("Returning to menu.")
            }
        }

        //******** Manage Bills ********//
        fun addBill() {
            val name = namePrompt(ObjectType.BILL)
            val amountDue = dollarPrompt(ObjectType.BILL)
            val dueDate = datePrompt()
            val account = existingAccountPrompt()
            // Asserting that name and amountDue are non-null because isUpdate = false in prompts
            val newBill = Bill(name!!, amountDue!!, dueDate!!, account)
            if (confirmationPrompt(newBill.toString(), ObjectType.BILL)) {
                billSet.add(newBill)
                LOGGER.debug("Bill added: {}", newBill)
                println("New bill added successfully.")
            } else {
                println("Returning to menu.")
            }
        }

        fun updateBill() {
            var oldBill = existingBillPrompt()
            println(oldBill)
            println("Press enter at any prompt to keep existing value.")
            var name = namePrompt(ObjectType.BILL, isUpdate = true)
            var amountDue = dollarPrompt(ObjectType.BILL, isUpdate = true)
            var dueDate = datePrompt(isUpdate = true)
            val account = existingAccountPrompt()
            if (name == null) name = oldBill.name
            if (amountDue == null) amountDue = oldBill.amount
            if (dueDate == null) dueDate = oldBill.dueDate
            val newBill = Bill(name, amountDue, dueDate, account)
            if (confirmationPrompt(newBill.toString(), ObjectType.BILL)) {
                LOGGER.debug("Bill updated: {} -> {}", oldBill, newBill)
                oldBill = newBill
                println("Bill updated successfully.")
            } else {
                println("Returning to menu.")
            }
        }

        fun deleteBill() {
            val bill = existingBillPrompt()
            if (confirmationPrompt(bill.toString(), ObjectType.BILL)) {
                billSet.remove(bill)
                LOGGER.debug("Bill removed: {}", bill)
                println("Bill deleted successfully.")
            } else {
                println("Returning to menu.")
            }
        }
    }
}