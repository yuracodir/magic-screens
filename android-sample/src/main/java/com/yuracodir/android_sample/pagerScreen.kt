package com.yuracodir.android_sample

import android.content.Context
import android.graphics.Color
import android.view.View
import com.estudio.magic.android.AndroidScreen
import com.estudio.magic.com.estudio.magic.Screen
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_RECYCLER
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_1
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_2
import com.yuracodir.android_sample.SamplePagerRouter.Companion.SCREEN_SAMPLE_3
import com.yuracodir.android_sample.screens.PagerRouter
import com.yuracodir.android_sample.screens.PagerScreen
import kotlinx.android.synthetic.main.screen_pager.view.*

class SamplePagerScreen(context: Context, router: ActivityRouter, val touchDelegate: Boolean = true) :
  PagerScreen<ActivityRouter>(context, router), View.OnClickListener {

  override val childRouter = SamplePagerRouter(this)
  override val screens = arrayOf(
    SCREEN_SAMPLE_1,
    if (touchDelegate) SCREEN_RECYCLER else SCREEN_SAMPLE_2,
    SCREEN_SAMPLE_3
  )
  override val layoutId = R.layout.screen_pager
  override fun instantiate(mark: String, args: Any?): Screen<*>? {
    return when (mark) {
      SCREEN_SAMPLE_1 -> SampleScreen(context, childRouter)
      SCREEN_SAMPLE_2 -> SampleScreen(context, childRouter)
      SCREEN_SAMPLE_3 -> SampleScreen(context, childRouter)
      SCREEN_RECYCLER -> SamplePagerScreen(context, router, false)
      else -> null
    }
  }

  override fun bind(view: View) {
    super.bind(view)
    view.next.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    childRouter.showFirstScreen()
  }

  override fun onPageChanged(position: Int) {
    super.onPageChanged(position)
    root.title.text = childRouter.toString()
  }
}

class SamplePagerRouter(private val pagerScreen: SamplePagerScreen) : PagerRouter(pagerScreen) {

  companion object {
    const val SCREEN_SAMPLE_1 = "sample1"
    const val SCREEN_SAMPLE_2 = "sample2"
    const val SCREEN_SAMPLE_3 = "sample3"
    const val SCREEN_RECYCLER = "recycler"
  }

  fun startColorScreen(color: Int, result: (Int) -> Unit) {
    pagerScreen.router.startColorScreen(color, result)
  }

  fun showFirstScreen() {
    show(SCREEN_SAMPLE_1)
  }
}

class SampleScreen(context: Context, router: SamplePagerRouter) :
  AndroidScreen<SamplePagerRouter>(context, router), View.OnClickListener {

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

  override fun pause() {
    super.pause()
    println("SCREEN: ${toString()}: pause")
  }

  override fun resume() {
    super.resume()
    println("SCREEN: ${toString()}: resume")
  }

  override fun destroy() {
    super.destroy()
    println("SCREEN: ${toString()}: destroy")
  }

  override fun create() {
    super.create()
    color = randomColor()
    println("SCREEN: ${toString()}: create")
  }

  override fun bind(view: View) {
    view.setOnClickListener(this)
  }

  private fun randomColor(): Int {
    val randomColor: () -> Int = { (Math.random() * 255).toInt() }
    return Color.rgb(randomColor(), randomColor(), randomColor())
  }
}