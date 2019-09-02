package com.yuracodir.sample.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout : FrameLayout {
  constructor(context: Context) : super(context)
  constructor(context: Context, attr: AttributeSet) : super(context, attr)
  constructor(context: Context, attr: AttributeSet, style: Int) : super(context, attr, style)
  constructor(context: Context, attr: AttributeSet, style: Int, defStyle: Int) : super(context, attr, style, defStyle)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec)
  }
}