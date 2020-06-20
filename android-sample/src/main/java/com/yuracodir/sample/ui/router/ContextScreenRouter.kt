package com.yuracodir.sample.ui.router

import android.content.Context
import com.yuracodir.screens.ContainerScreen
import com.yuracodir.screens.Screen
import com.yuracodir.screens.ScreenRouter

open class ContextScreenRouter(val context: Context, container: ContainerScreen) : ScreenRouter(container) {
  fun root(block: ContextScreenRouter.() -> Screen<*>) {
    root(block.invoke(this))
  }

  fun forward(block: ContextScreenRouter.() -> Screen<*>) {
    forward(block.invoke(this))
  }

  fun replace(block: ContextScreenRouter.() -> Screen<*>) {
    replace(block.invoke(this))
  }
}