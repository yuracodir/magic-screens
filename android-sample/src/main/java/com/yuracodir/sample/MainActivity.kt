package com.yuracodir.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.estudio.magic.ContainerScreen
import com.estudio.magic.Screen
import com.estudio.magic.ScreenRouter
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.sample.data.models.VacancyPreviewDto
import com.yuracodir.sample.ui.DetailsScreen
import com.yuracodir.sample.ui.VacanciesScreen

class MainActivity : AppCompatActivity(), ContainerScreen {
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

  fun instantiate(mark: String, args: Any?): Screen<*>? {
    return when (mark) {
      VacanciesScreen.Name -> VacanciesScreen(this, childRouter)
      DetailsScreen.Name -> DetailsScreen(this, childRouter, args as VacancyPreviewDto)
      else -> null
    }
  }

  fun openLink(title: String, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
      startActivity(Intent.createChooser(intent, title))
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

class ActivityRouter(private val container: MainActivity) : ScreenRouter(container) {

  override fun instantiate(mark: String, args: Any?): Screen<*>? {
    return container.instantiate(mark, args)
  }

  fun startMainScreen() {
    root(VacanciesScreen.Name)
  }

  fun startDetailsScreen(data: VacancyPreviewDto) {
    forward(DetailsScreen.Name, data)
  }

  fun openLink(title:String, url: String) {
    container.openLink(title, url)
  }
}