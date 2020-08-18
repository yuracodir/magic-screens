package com.yuracodir.sample.ui

import android.content.Context
import android.widget.FrameLayout
import com.yuracodir.sample.R
import com.yuracodir.sample.inflate
import com.yuracodir.sample.ui.router.ContextScreenRouter
import com.yuracodir.screens.Screen
import com.yuracodir.screens.android.AndroidContainerScreen
import com.yuracodir.screens.android.AndroidScreen

class RootScreen(context: Context, router: ContextScreenRouter)
  : AndroidContainerScreen<ContextScreenRouter>(context, router) {
  override val root = inflate(context, R.layout.screen_root) as FrameLayout
  override val childRouter = ContextScreenRouter(context, this)

  override fun attach(screen: Screen<*>) {
    (screen as? AndroidScreen<*>)?.let {
      root.addView(it.root)
    }
  }

  override fun detach(screen: Screen<*>) {
    (screen as? AndroidScreen<*>)?.let {
      root.removeView(it.root)
    }
  }

  override fun create() {
    super.create()
    childRouter.forward {
      AlbumListScreen(context, this)
    }
  }
}