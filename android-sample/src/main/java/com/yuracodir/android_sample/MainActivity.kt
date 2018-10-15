package com.yuracodir.android_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.estudio.magic.android.AndroidScreen
import com.estudio.magic.com.estudio.magic.ContainerScreen
import com.estudio.magic.com.estudio.magic.Screen
import com.estudio.magic.com.estudio.magic.ScreenRouter
import com.yuracodir.android_sample.ActivityRouter.Companion.SCREEN_COLOR
import com.yuracodir.android_sample.ActivityRouter.Companion.SCREEN_FORWARD
import com.yuracodir.android_sample.ActivityRouter.Companion.SCREEN_PAGER
import com.yuracodir.android_sample.ActivityRouter.Companion.SCREEN_REPLACE
import com.yuracodir.android_sample.ActivityRouter.Companion.SCREEN_ROOT

class MainActivity : AppCompatActivity(), ContainerScreen {

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

  override val childRouter = ActivityRouter(this)
  private lateinit var containerView: ViewGroup

  fun instantiate(mark: String, args: Any?): Screen<*>? {
    return when (mark) {
      SCREEN_ROOT -> MainScreen(this, childRouter)
      SCREEN_FORWARD -> MainScreen(this, childRouter)
      SCREEN_REPLACE -> MainScreen(this, childRouter)
      SCREEN_COLOR -> ColorScreen(this, childRouter, args as? ColorArguments)
      SCREEN_PAGER -> SamplePagerScreen(this, childRouter)
      else -> null
    }
  }

  override fun onBackPressed() {
    if (childRouter.currentScreen?.onBack() == false) {
      super.onBackPressed()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    containerView = findViewById(R.id.container)

    childRouter.startMainScreen()
  }
}

class ActivityRouter(val container: MainActivity) : ScreenRouter(container) {
  fun startColorScreen(color: Int, result: (Int) -> Unit) {
    forward(SCREEN_COLOR)
  }

  override fun instantiate(mark: String, args: Any?): Screen<*>? {
    return container.instantiate(mark, args)
  }

  fun startMainScreen() {
    root(SCREEN_ROOT)
  }

  companion object {
    const val SCREEN_ROOT = "root"
    const val SCREEN_FORWARD = "forward"
    const val SCREEN_REPLACE = "replace"
    const val SCREEN_COLOR = "color"
    const val SCREEN_PAGER = "pager"
  }
}
