package com.vtnd.duynn.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vtnd.duynn.presentation.ui.auth.AuthActivity
import com.vtnd.duynn.presentation.ui.main.MainActivity
import com.vtnd.duynn.utils.extension.observeEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModel<SplashViewModel>(state = emptyState())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.authEvent.observeEvent(this) {
            Timber.i("viewModel.authEvent")
            val clazz = if (it) MainActivity::class.java else AuthActivity::class.java
            val intent = Intent(this, clazz).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
            finish()
        }
    }
}
