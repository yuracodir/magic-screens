package com.estudio.magic.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.estudio.magic.ContainerScreen
import com.estudio.magic.Router
import com.estudio.magic.Screen
import com.estudio.magic.ScreenState

abstract class ContainerAndroidScreen<Ro : Router, A : Any>(context: Context, router: Ro) :
  AndroidScreen<Ro, A>(context, router), ContainerScreen {

  protected lateinit var container: ViewGroup

  override fun bind(view: View) {
    container = view.findViewById(R.id.container)
  }

  override fun pause() {
    super.pause()
    if (childRouter.isEmpty()) {
      return
    }
    childRouter.pauseScreen(childRouter.currentScreen())
  }

  override fun resume() {
    super.resume()
    if (childRouter.isEmpty()) {
      return
    }
    childRouter.resumeScreen(childRouter.currentScreen())
  }

  override fun destroy() {
    super.destroy()
    if (childRouter.isEmpty()) {
      return
    }
    childRouter.destroyScreen(childRouter.currentScreen())
  }

  override fun onBack(): Boolean {
    val childGoBack = !childRouter.isEmpty() && childRouter.currentScreen().onBack()
    return childGoBack || super.onBack()
  }

  override fun attach(screen: Screen<*, *>) {
    if (screen is AndroidScreen<*, *>) {
      container.addView(screen.root)
    }
  }

  override fun detach(screen: Screen<*, *>) {
    if (screen is AndroidScreen<*, *>) {
      container.removeView(screen.root)
    }
  }
}

abstract class AndroidScreen<R : Router, A : Any>(val context: Context, override var router: R) : Screen<R, A> {
  lateinit var root: View
  override var state = ScreenState.NONE
  override lateinit var args: A

  abstract val layoutId: Int

  abstract fun bind(view: View)

  override fun create() {
    root = View.inflate(context, layoutId, null)
    bind(root)
  }

  override fun pause() {
  }

  override fun resume() {
  }

  override fun destroy() {
  }

  override fun onBack(): Boolean {
    return router.back()
  }
}