package com.vtnd.duynn.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vtnd.duynn.R
import com.vtnd.duynn.databinding.ActivityMainBinding
import com.vtnd.duynn.presentation.ui.auth.AuthActivity
import com.vtnd.duynn.utils.extension.*
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
class MainActivity : AppCompatActivity() {

    private var currentNavController: StateFlow<NavController>? = null
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModel<MainViewModel>(state = emptyState())

    var onSupportNavigateUp: (() -> Boolean)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState === null) {
            setupBottomNavigationBar()
        } else {
            dismissAlertDialog()
        }
        bindVM()
    }

    private fun bindVM() {
        viewModel.logoutEvent.observe(this) {
            Timber.d("[LOGOUT]")
            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
            finish()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.home,
            R.navigation.profile,
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = binding.navView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        onSupportNavigateUp?.invoke()?.let { return it }
        return currentNavController?.value?.navigateUp() ?: false
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Timber.d("[onActivityResult] { requestCode: $requestCode, resultCode: $resultCode, data: $data }")

        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            ?: return)
            .childFragmentManager
            .fragments
            .forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    fun hideBottomNav() = binding.navView.gone()

    fun showBottomNav() = binding.navView.visible()
}