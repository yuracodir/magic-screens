package com.estudio.magic.android

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.estudio.magic.ContainerScreen
import com.estudio.magic.Router
import com.estudio.magic.Screen
import com.estudio.magic.ScreenRouter

abstract class AndroidContainerScreen<Ro : Router>(context: Context, router: Ro) :
  AndroidScreen<Ro>(context, router),
  ContainerScreen {

  protected lateinit var container: ViewGroup

  override fun create() {
    super.create()
    container = root.findViewById(R.id.container)
  }

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

  override fun onBack(): Boolean {
    val childGoBack = childRouter.currentScreen?.onBack() == true
    return childGoBack || super.onBack()
  }

  override fun attach(screen: Screen<*>) {
    if (screen is AndroidScreen<*>) {
      container.addView(screen.root)
    }
  }

  override fun detach(screen: Screen<*>) {
    if (screen is AndroidScreen<*>) {
      container.removeView(screen.root)
    }
  }
}

abstract class AndroidScreen<R : Router>(
  protected val context: Context,
  override var router: R) : Screen<R> {

  abstract val root: View

  override fun create() {
  }

  override fun pause() {
  }

  override fun resume() {
  }

  override fun destroy() {
  }

  override fun onBack(): Boolean {
    val router = this.router
    if (router is ScreenRouter) {
      return router.back()
    }
    return false
  }

  fun hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(root.windowToken, 0)
  }
}