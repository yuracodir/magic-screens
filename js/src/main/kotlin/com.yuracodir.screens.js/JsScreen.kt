package com.yuracodir.screens.js

import com.yuracodir.screens.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

abstract class JsContainerScreen<R : Router>(override var router: R) :
    JsScreen<R>(router), ContainerScreen {
  protected lateinit var container: HTMLElement

  override fun create() {
    super.create()
    container = root.getElementsByTagName("container")[0] as HTMLElement
  }

  override fun resume() {
    super.resume()
    childRouter.navigator.resume()
  }

  override fun pause() {
    super.pause()
    childRouter.navigator.pause()
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
    if (screen is JsScreen<*>) {
      container.appendChild(screen.root)
    }
  }

  override fun detach(screen: Screen<*>) {
    if (screen is JsScreen<*>) {
      container.removeChild(screen.root)
    }
  }
}

abstract class JsScreen<R : Router>(override var router: R) : CallbackScreen<R>() {

  abstract var root: Element

  override fun onBack(): Boolean {
    super.onBack()
    val router = this.router
    if (router is ScreenRouter) {
      return router.back()
    }
    return false
  }
}