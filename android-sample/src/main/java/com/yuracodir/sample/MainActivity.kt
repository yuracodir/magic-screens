package com.yuracodir.sample

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.yuracodir.sample.ui.RootScreen
import com.yuracodir.sample.ui.router.ContextScreenRouter
import com.yuracodir.screens.ContainerScreen
import com.yuracodir.screens.Screen
import com.yuracodir.screens.android.AndroidScreen

class MainActivity : Activity(), ContainerScreen {
  override val childRouter = ContextScreenRouter(this, this)
  private lateinit var containerView: ViewGroup

  override fun attach(screen: Screen<*>) {
    if (screen is AndroidScreen) {
      containerView.addView(screen.root)
    }
  }

  override fun detach(screen: Screen<*>) {
    if (screen is AndroidScreen) {
      containerView.removeView(screen.root)
    }
  }

  override fun onPause() {
    super.onPause()
    childRouter.navigator.pause()
  }

  override fun onResume() {
    super.onResume()
    childRouter.navigator.resume()
  }

  override fun onDestroy() {
    super.onDestroy()
    childRouter.navigator.destroy()
  }

  override fun onBackPressed() {
    if (childRouter.currentScreen?.onBack() == false) {
      super.onBackPressed()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_container)
    containerView = findViewById(R.id.container)

    childRouter.root {
      RootScreen(context, this)
    }
  }
}