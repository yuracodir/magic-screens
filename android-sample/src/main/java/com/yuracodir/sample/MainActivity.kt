package com.yuracodir.sample

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.estudio.magic.ContainerScreen
import com.estudio.magic.Screen
import com.estudio.magic.ScreenRouter
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.sample.data.models.VacancyPreviewDto
import com.yuracodir.sample.ui.DetailsScreen
import com.yuracodir.sample.ui.VacanciesScreen

class MainActivity : Activity(), ContainerScreen {
  override val childRouter = ActivityRouter(this)
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
    setContentView(R.layout.activity_main)
    containerView = findViewById(R.id.container)

    childRouter.startMainScreen()
  }
}

class ActivityRouter(private val container: MainActivity) : ScreenRouter(container) {
  fun startMainScreen() {
    root(VacanciesScreen(container, this))
  }

  fun startDetailsScreen(data: VacancyPreviewDto) {
    forward(DetailsScreen(container, this, data))
  }
}