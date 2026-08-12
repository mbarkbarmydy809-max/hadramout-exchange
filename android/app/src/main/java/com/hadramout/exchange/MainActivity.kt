package com.hadramout.exchange

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private lateinit var nav: LinearLayout
    private lateinit var repository: ExchangeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ExchangeRepository(this)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.screen_content)
        nav = findViewById(R.id.bottom_navigation)
        setupNavigation()
        showHome()
    }

    private fun setupNavigation() {
        findViewById<Button>(R.id.nav_home).setOnClickListener { showHome() }
        findViewById<Button>(R.id.nav_deposit).setOnClickListener { showDeposit() }
        findViewById<Button>(R.id.nav_withdrawal).setOnClickListener { showWithdrawal() }
        findViewById<Button>(R.id.nav_transactions).setOnClickListener { showTransactions() }
        findViewById<Button>(R.id.nav_admin).setOnClickListener { showAdmin() }
    }

    private fun showHome() {
        render(R.layout.fragment_home)
        findViewById<TextView>(R.id.home_balance).text = repository.balance
        findViewById<Button>(R.id.home_deposit_button).setOnClickListener { showDeposit() }
        findViewById<Button>(R.id.home_withdraw_button).setOnClickListener { showWithdrawal() }
        findViewById<Button>(R.id.home_transactions_button).setOnClickListener { showTransactions() }
        findViewById<Button>(R.id.home_admin_button).setOnClickListener { showAdmin() }
        findViewById<TextView>(R.id.home_mode).text =
            if (SupabaseService.isDemoMode) getString(R.string.demo_mode) else getString(R.string.live_mode)
        selectNav(R.id.nav_home)
    }

    private fun showDeposit() {
        render(R.layout.fragment_deposit)
        val amountInput = findViewById<EditText>(R.id.deposit_amount)
        findViewById<Button>(R.id.deposit_submit).setOnClickListener {
            val amount = amountInput.text.toString().trim()
            if (amount.isBlank()) {
                amountInput.error = getString(R.string.required_amount)
                return@setOnClickListener
            }
            repository.addTransaction("طلب إيداع", "دائن", "+ $amount ريال")
            toast(getString(R.string.request_sent))
            showTransactions()
        }
        findViewById<Button>(R.id.deposit_back).setOnClickListener { showHome() }
        selectNav(R.id.nav_deposit)
    }

    private fun showWithdrawal() {
        render(R.layout.fragment_withdrawal)
        val amountInput = findViewById<EditText>(R.id.withdrawal_amount)
        findViewById<Button>(R.id.withdrawal_submit).setOnClickListener {
            val amount = amountInput.text.toString().trim()
            if (amount.isBlank()) {
                amountInput.error = getString(R.string.required_amount)
                return@setOnClickListener
            }
            repository.addTransaction("طلب سحب", "مدين", "- $amount ريال")
            toast(getString(R.string.request_sent))
            showTransactions()
        }
        findViewById<Button>(R.id.withdrawal_back).setOnClickListener { showHome() }
        selectNav(R.id.nav_withdrawal)
    }

    private fun showTransactions() {
        render(R.layout.fragment_transactions)
        val list = findViewById<LinearLayout>(R.id.transactions_list)
        repository.allTransactions().forEach { transaction ->
            val item = LayoutInflater.from(this).inflate(R.layout.item_transaction, list, false)
            item.findViewById<TextView>(R.id.transaction_title).text = transaction.title
            item.findViewById<TextView>(R.id.transaction_meta).text =
                "${transaction.id}  •  ${transaction.date}"
            item.findViewById<TextView>(R.id.transaction_amount).text = transaction.amount
            item.findViewById<TextView>(R.id.transaction_status).text = transaction.status
            list.addView(item)
        }
        findViewById<Button>(R.id.transactions_back).setOnClickListener { showHome() }
        selectNav(R.id.nav_transactions)
    }

    private fun showAdmin() {
        render(R.layout.fragment_admin_dashboard)
        findViewById<TextView>(R.id.admin_total_transactions).text =
            repository.allTransactions().size.toString()
        findViewById<Button>(R.id.admin_back).setOnClickListener { showHome() }
        findViewById<Button>(R.id.admin_refresh).setOnClickListener {
            findViewById<TextView>(R.id.admin_last_sync).text = getString(R.string.synced_now)
            toast(getString(R.string.dashboard_updated))
        }
        selectNav(R.id.nav_admin)
    }

    private fun render(layout: Int) {
        content.removeAllViews()
        layoutInflater.inflate(layout, content, true)
        content.post { content.parent.requestChildFocus(content, content) }
    }

    private fun selectNav(selectedId: Int) {
        for (index in 0 until nav.childCount) {
            val child = nav.getChildAt(index)
            child.isSelected = child.id == selectedId
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}