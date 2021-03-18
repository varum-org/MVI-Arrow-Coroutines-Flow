package com.vtnd.duynn.presentation.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.vtnd.duynn.utils.extension.launchWhenStartedUntilStopped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
abstract class BaseFragment<
        I : MVIIntent,
        S : MVIViewState,
        E : MVISingleEvent,
        VM : MVIViewModel<I, S, E>>(
    @LayoutRes layoutRes: Int
) : Fragment(layoutRes), MVIView<I, S, E> {

    protected abstract val viewModel: VM
    protected abstract val viewBinding: ViewBinding

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("$this::onCreate: $savedInstanceState")
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = super.onCreateView(inflater, container, savedInstanceState)!!
        .also { Timber.d("$this::onCreateView") }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("$this::onViewCreated: $view, $savedInstanceState")
        setUpView(view, savedInstanceState)
        bindView()
    }

    @CallSuper
    override fun onStop() {
        onHideSoftKeyBoard()
        super.onStop()
        Timber.d("$this::onStop")
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("$this::onDestroyView")
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("$this::onDestroy")
    }

    open fun onHideSoftKeyBoard() {
        val inputMng: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMng.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    protected abstract fun setUpView(view: View, savedInstanceState: Bundle?)

    private fun bindView() {
        // observe view model
        viewModel.viewState
            .onEach { render(it) }
            .launchWhenStartedUntilStopped(this)

        // observe single event
        viewModel.singleEvent
            .onEach { handleSingleEvent(it) }
            .launchWhenStartedUntilStopped(this)

        // pass view intent to view model
        viewIntents()
            .onEach { viewModel.processIntent(it) }
            .launchIn(lifecycleScope)
    }
}
