package com.estudio.magic.js

import com.estudio.magic.com.estudio.magic.ContainerScreen
import com.estudio.magic.com.estudio.magic.Router
import com.estudio.magic.com.estudio.magic.Screen
import com.estudio.magic.com.estudio.magic.ScreenRouter
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get


abstract class JsContainerScreen<R : Router>(override var router: R) :
  JsScreen<R>(router), ContainerScreen {

  lateinit var container: HTMLElement

  override fun bind(view: Element) {
    container = view.getElementsByClassName("container")[0] as HTMLElement
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

abstract class JsScreen<R : Router>(override var router: R) : Screen<R> {

  abstract var root: Element

  abstract fun bind(view: Element)

  override fun create() {
    bind(root)
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

  override fun pause() {

  }

  override fun resume() {

  }

}