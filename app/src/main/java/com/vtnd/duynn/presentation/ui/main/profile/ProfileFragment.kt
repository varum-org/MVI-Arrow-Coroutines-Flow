package com.vtnd.duynn.presentation.ui.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vtnd.duynn.R
import com.vtnd.duynn.databinding.FragmentProfileBinding
import com.vtnd.duynn.utils.extension.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel by viewModel<ProfileViewModel>(state = emptyState())
    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }
}
