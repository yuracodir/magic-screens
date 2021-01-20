package com.yuracodir.screens.android

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.yuracodir.screens.CallbackScreen
import com.yuracodir.screens.ContainerScreen
import com.yuracodir.screens.Router
import com.yuracodir.screens.ScreenRouter

abstract class AndroidContainerScreen<Ro : Router>(context: Context, router: Ro) :
    AndroidScreen<Ro>(context, router), ContainerScreen {
  override fun pause() {
    super.pause()
    childRouter.navigator.pause()
  }

  override fun resume() {
    super.resume()
    childRouter.navigator.resume()
  }

  override fun destroy() {
    super.destroy()
    childRouter.navigator.destroy()
  }

  override fun back(): Boolean {
    val childGoBack = childRouter.currentScreen?.back() == true
    return childGoBack || super.back()
  }
}

abstract class AndroidScreen<R : Router>(
  protected val context: Context,
  override var router: R) : CallbackScreen<R>() {
  abstract val root: View

  override fun back(): Boolean {
    val router = this.router
    if (router is ScreenRouter) {
      return super.back() || router.back()
    }
    return super.back()
  }

  fun hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(root.windowToken, 0)
  }
}