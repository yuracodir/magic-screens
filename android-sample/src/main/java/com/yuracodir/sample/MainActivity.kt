package com.yuracodir.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.estudio.magic.ContainerScreen
import com.estudio.magic.Screen
import com.estudio.magic.ScreenNavigator
import com.estudio.magic.ScreenRouter
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_COLOR
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_FORWARD
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_PAGER
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_REPLACE
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_ROOT


class MainActivity : AppCompatActivity(), ContainerScreen {

  override fun attach(screen: Screen<*>) {
    if (screen is AndroidScreen) {
      containerView.addView(screen.root)
      screen.root.visibility = View.VISIBLE
    }
  }

  override fun detach(screen: Screen<*>) {
    if (screen is AndroidScreen) {
      screen.root.visibility = View.GONE
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

  fun animateForward(prev: Screen<*>?, next: Screen<*>) {
    TransitionManager.endTransitions(containerView)
    TransitionManager.beginDelayedTransition(
      containerView,
      TransitionSet().apply {
        addTransition(
          Slide(Gravity.END)
            .addTarget((next as AndroidScreen<*>).root)
        )
        prev?.let {
          addTransition(
            Slide(Gravity.START)
              .addTarget((prev as AndroidScreen<*>).root)
              .addListener(RemoveViewOnEndTransitionListener(prev.root))
          )
        }
      })
  }

  fun animateBackward(prev: Screen<*>?, next: Screen<*>) {
    TransitionManager.endTransitions(containerView)
    TransitionManager.beginDelayedTransition(
      containerView,
      TransitionSet().apply {
        addTransition(
          Slide(Gravity.START)
            .addTarget((next as AndroidScreen<*>).root)
        )
        prev?.let {
          addTransition(
            Slide(Gravity.END)
              .addTarget((prev as AndroidScreen<*>).root)
              .addListener(RemoveViewOnEndTransitionListener(prev.root))
          )
        }
      })
  }
}

class RemoveViewOnEndTransitionListener(val view: View) : Transition.TransitionListener {
  override fun onTransitionResume(transition: Transition?) {
  }

  override fun onTransitionPause(transition: Transition?) {
  }

  override fun onTransitionCancel(transition: Transition?) {
  }

  override fun onTransitionStart(transition: Transition?) {
  }

  override fun onTransitionEnd(transition: Transition) {
    (view.parent as ViewGroup).removeView(view)
    transition.removeListener(this)
  }
}

class AnimatedScreenNavigator(val container: MainActivity) : ScreenNavigator(container) {
  override fun forwardScreen(screen: Screen<*>) {
    container.animateForward(lastScreen, screen)
    super.forwardScreen(screen)
  }

  override fun replaceScreen(screen: Screen<*>) {
    container.animateBackward(lastScreen, screen)
    super.replaceScreen(screen)
  }
}

class ActivityRouter(val container: MainActivity) : ScreenRouter(container, AnimatedScreenNavigator(container)) {
  fun startColorScreen() {
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
