package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeeplinkActivity : AppCompatActivity() {
    private val viewModel: DeeplinkActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.handleIntent(intent)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.handleIntent(intent)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onStop() {
        super.onStop()
        println("Wallet DeeplinkActivity.onStop() called")
    }
}
