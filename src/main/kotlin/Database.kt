class Database {
    companion object {
        val accountSet = mutableSetOf<Account>()
        val billSet = mutableSetOf<Bill>()
        var selectedAccount: Account? = null
    }

    //********* Retrieve Items **********//
    fun accountByName(name: String): Account? {
        return accountSet.firstOrNull { it.name == name }
    }
    fun billByName(name: String): Bill? {
        return billSet.firstOrNull { it.name == name }
    }

    //******** Manage Accounts ********//
    fun addAccount() {
        TODO("Add new account to accountSet.")
    }

    fun updateAccount() {
        TODO("Update existing account.")
        //TODO: trigger account.editAccount()
    }

    fun deleteAccount() {
        TODO("Delete account from accountSet.")
    }

    //******** Manage Bills ********//
    fun addBill() {
        TODO("Add new bill to billSet.")
    }

    fun updateBill() {
        TODO("Update existing bill.")
        //TODO: trigger bill.editBill()
    }

    fun deleteBill() {
        TODO("Delete bill from billSet.")
    }

    //******** File Handlers ********//
    fun importHandler(filename: String = "default.json") {
        TODO("Import file from default path with filename.")
    }

    fun exportHandler(filename: String = "default.json") {
        TODO("Export file to default path with filename.")
    }
}