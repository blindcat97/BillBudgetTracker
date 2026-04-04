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
                // Ridiculous step to relink bill.account entries to accountSet entries
                billSet.forEach { it.account = accountByName(it.account.name)!! }
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

        fun accountByName(name: String?): Account? {
            return accountSet.firstOrNull { it.name == name }
        }

        fun existingAccountPrompt(): Account? {
            var testAccount: Account
            while (true) {
                accountSet.forEach { println(it) }
                if (selectedAccount != null) {
                    println("Active account: ${selectedAccount!!.name}. Leave prompt blank to use this account.")
                }
                val name = nameInput(OType.ACCOUNT)
                if (selectedAccount != null && name == null) {
                    return selectedAccount!!
                } else if (name == null) return null
                if (accountByName(name) == null) {
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
                val name = nameInput(OType.BILL)
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

        fun removeAccount(target: Account) {
            accountSet.remove(target)
            billSet.removeIf { it.account == target }
        }


        //******** Manage Bills ********//

        fun removeBill(target: Bill) {
            billSet.remove(target)
        }
    }
}