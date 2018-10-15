package com.yuracodir.android_sample

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import com.estudio.magic.android.AndroidScreen
import com.estudio.magic.com.estudio.magic.ScreenRouter
import kotlinx.android.synthetic.main.screen_color.view.*

class ColorScreen(
  context: Context,
  router: ScreenRouter,
  private val args: ColorArguments?
) :
  AndroidScreen<ScreenRouter>(context, router),
  SeekBar.OnSeekBarChangeListener, View.OnClickListener {

  override fun onClick(v: View?) {
    onBack()
  }

  override val layoutId = R.layout.screen_color
  private val color = IntArray(3)

  override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    when (seekBar?.id) {
      R.id.red -> color[0] = seekBar.progress
      R.id.green -> color[1] = seekBar.progress
      R.id.blue -> color[2] = seekBar.progress
    }
    root.color.setBackgroundColor(Color.rgb(color[0], color[1], color[2]))
  }

  override fun onStartTrackingTouch(seekBar: SeekBar?) {}

  override fun onStopTrackingTouch(seekBar: SeekBar?) {}

  override fun bind(view: View) {
    view.red.setOnSeekBarChangeListener(this)
    view.green.setOnSeekBarChangeListener(this)
    view.blue.setOnSeekBarChangeListener(this)
    val arguments = args
    if (arguments != null) {
      view.red.progress = Color.red(arguments.color)
      view.green.progress = Color.green(arguments.color)
      view.blue.progress = Color.blue(arguments.color)
    }
    view.toolbar.setNavigationOnClickListener(this)
  }

  override fun onBack(): Boolean {
    args?.result?.invoke(Color.rgb(color[0], color[1], color[2]))
    router.back()
    return true
  }
}