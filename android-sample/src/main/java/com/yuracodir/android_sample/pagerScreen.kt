package com.yuracodir.android_sample

import android.content.Context
import android.graphics.Color
import android.view.View
import com.estudio.magic.Screen
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_PAGER
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_1
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_2
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_3
import com.yuracodir.android_sample.screens.PagerRouter
import com.yuracodir.android_sample.screens.PagerScreen

class SamplePagerScreen(context: Context, router: ActivityRouter) :
  PagerScreen<ActivityRouter, ColorArguments>(context, router) {
  override val childRouter = SamplePagerRouter(this)
  override val screens = arrayOf(
    SCREEN_SAMPLE_1,
    SCREEN_SAMPLE_2,
    SCREEN_SAMPLE_3
  )
  override val layoutId = R.layout.screen_pager

  override fun instantiate(mark: String): Screen<*, *>? {
    return when (mark) {
      SCREEN_SAMPLE_1 -> SampleScreen(context, childRouter)
      SCREEN_SAMPLE_2 -> SampleScreen(context, childRouter)
      SCREEN_SAMPLE_3 -> SampleScreen(context, childRouter)
      SCREEN_PAGER -> SamplePagerScreen(context, router)
      else -> null
    }
  }

  override fun create() {
    super.create()
    if (args != null) {
      val screen = instantiate(SCREEN_PAGER)
      if (screen != null) {
        adapter.add(screen as AndroidScreen<*, *>)
        childRouter.lifecycle.create(screen)
      }
    }
  }
}

class SamplePagerRouter(val pagerScreen: SamplePagerScreen) : PagerRouter(pagerScreen) {

  companion object {
    const val SCREEN_SAMPLE_1 = "sample1"
    const val SCREEN_SAMPLE_2 = "sample2"
    const val SCREEN_SAMPLE_3 = "sample3"
    const val SCREEN_PAGER = "pager"
  }

  fun startColorScreen(color: Int, result: (Int) -> Unit) {
    pagerScreen.router.startColorScreen(color, result)
  }
}

class SampleScreen(context: Context, router: SamplePagerRouter) :
  AndroidScreen<SamplePagerRouter, Any>(context, router), View.OnClickListener {

  override val layoutId = R.layout.screen_sample
  var color: Int = 0
    set(value) {
      field = value
      root.setBackgroundColor(value)
    }

  override fun onClick(v: View?) {
    router.startColorScreen(color) {
      this.color = it
    }
  }

  override fun create() {
    super.create()
    color = randomColor()
  }

  override fun bind(view: View) {
    view.setOnClickListener(this)
  }


  private fun randomColor(): Int {
    val randomColor: () -> Int = { (Math.random() * 255).toInt() }
    return Color.rgb(randomColor(), randomColor(), randomColor())
  }
}