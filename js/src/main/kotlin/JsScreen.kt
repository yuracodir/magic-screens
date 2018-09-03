package com.estudio.magic.js

import com.estudio.magic.ContainerScreen
import com.estudio.magic.Router
import com.estudio.magic.Screen
import com.estudio.magic.ScreenState
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.browser.document


abstract class JsContainerScreen<R : Router, A : Any>(override var router: R) :
  JsScreen<R, A>(router), ContainerScreen {

  lateinit var container: HTMLElement

  override fun bind(view: Element) {
    container = view.getElementsByClassName("container")[0] as HTMLElement
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
    if (screen is JsScreen<*, *>) {
      container.appendChild(screen.root)
    }
  }

  override fun detach(screen: Screen<*, *>) {
    if (screen is JsScreen<*, *>) {
      container.removeChild(screen.root)
    }
  }
}

abstract class JsScreen<R : Router, A : Any>(override var router: R) : Screen<R, A> {

  override var state = ScreenState.NONE
  override lateinit var args: A

  abstract var root: Element

  abstract fun bind(view: Element)

  override fun create() {
    bind(root)
  }

  override fun destroy() {

  }

  override fun onBack(): Boolean {
    return router.back()
  }

  override fun pause() {

  }

  override fun resume() {

  }

}