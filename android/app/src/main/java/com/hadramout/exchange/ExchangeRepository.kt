package com.hadramout.exchange

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TransactionItem(
    val id: String,
    val title: String,
    val type: String,
    val amount: String,
    val date: String,
    val status: String,
)

class ExchangeRepository(context: Context) {
    private val preferences = context.getSharedPreferences("exchange_state", Context.MODE_PRIVATE)
    private val transactions = mutableListOf<TransactionItem>()

    var balance: String = preferences.getString("balance", "1,250,000") ?: "1,250,000"
        private set

    init {
        restore()
        if (transactions.isEmpty()) {
            transactions += listOf(
                TransactionItem("HX-1024", "استلام حوالة", "دائن", "+ 350,000 ريال", "12 أغسطس 2026", "مكتملة"),
                TransactionItem("HX-1023", "شراء دولار أمريكي", "مدين", "- 200 دولار", "10 أغسطس 2026", "مكتملة"),
                TransactionItem("HX-1022", "إيداع نقدي", "دائن", "+ 500,000 ريال", "08 أغسطس 2026", "قيد المراجعة"),
            )
        }
    }

    fun allTransactions(): List<TransactionItem> = transactions.toList()

    fun addTransaction(title: String, type: String, amount: String): TransactionItem {
        val item = TransactionItem(
            id = "HX-${1000 + transactions.size + 1}",
            title = title,
            type = type,
            amount = amount,
            date = "اليوم",
            status = "قيد المراجعة",
        )
        transactions.add(0, item)
        save()
        return item
    }

    fun setBalance(value: String) {
        balance = value
        save()
    }

    private fun restore() {
        val encoded = preferences.getString("transactions", null) ?: return
        runCatching {
            val array = JSONArray(encoded)
            for (index in 0 until array.length()) {
                val json = array.getJSONObject(index)
                transactions += TransactionItem(
                    json.getString("id"),
                    json.getString("title"),
                    json.getString("type"),
                    json.getString("amount"),
                    json.getString("date"),
                    json.getString("status"),
                )
            }
        }
    }

    private fun save() {
        val array = JSONArray()
        transactions.forEach {
            array.put(JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("type", it.type)
                put("amount", it.amount)
                put("date", it.date)
                put("status", it.status)
            })
        }
        preferences.edit()
            .putString("transactions", array.toString())
            .putString("balance", balance)
            .apply()
    }
}