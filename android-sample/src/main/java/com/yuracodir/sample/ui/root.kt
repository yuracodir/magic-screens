package com.yuracodir.sample.ui

import android.content.Context
import com.yuracodir.sample.R
import com.yuracodir.sample.inflate
import com.yuracodir.sample.ui.router.ContextScreenRouter
import com.yuracodir.screens.Screen
import com.yuracodir.screens.android.AndroidContainerScreen
import com.yuracodir.screens.android.AndroidScreen
import kotlinx.android.synthetic.main.activity_container.view.*

class RootScreen(context: Context, router: ContextScreenRouter)
  : AndroidContainerScreen<ContextScreenRouter>(context, router) {
  override val root = inflate(context, R.layout.screen_root)
  override val childRouter = ContextScreenRouter(context, this)

  override fun attach(screen: Screen<*>) {
    (screen as? AndroidScreen<*>)?.let {
      root.container.addView(it.root)
    }
  }

  override fun detach(screen: Screen<*>) {
    (screen as? AndroidScreen<*>)?.let {
      root.container.removeView(it.root)
    }
  }

  override fun create() {
    super.create()
    childRouter.forward {
      AlbumListScreen(context, this)
    }
  }
}