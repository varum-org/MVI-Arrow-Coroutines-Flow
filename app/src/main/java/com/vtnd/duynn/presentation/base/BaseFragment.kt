package com.vtnd.duynn.presentation.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("$this::onCreate: $savedInstanceState")
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("$this::onViewCreated: $view, $savedInstanceState")
        setUpView()
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

    protected abstract fun setUpView()
    protected abstract fun bindView()
}
