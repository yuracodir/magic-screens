package com.yuracodir.sample

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.yuracodir.magic.ContainerScreen
import com.yuracodir.magic.Screen
import com.yuracodir.magic.ScreenRouter
import com.yuracodir.magic.android.AndroidScreen
import com.yuracodir.sample.data.models.PhotoDto
import com.yuracodir.sample.ui.PhotoScreen

class PhotoActivity : AppCompatActivity(), ContainerScreen {
  companion object {
    const val bundlePhotoList = "bundle_list"
    const val bundlePhotoSelected = "bundle_selected"
  }

  override val childRouter = PhotoActivityRouter(this)
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
    setContentView(R.layout.activity_container)
    containerView = findViewById(R.id.container)
    val intent = intent
    if (intent == null) {
      finish()
      return
    }
    val item = intent.getParcelableExtra<PhotoDto>(bundlePhotoSelected)
    val items = intent.getParcelableArrayListExtra<PhotoDto>(bundlePhotoList)
    childRouter.startPhotoScreen(item, items)
  }
}

class PhotoActivityRouter(private val activity: PhotoActivity) : ScreenRouter(activity) {
  fun startPhotoScreen(item: PhotoDto, items: List<PhotoDto>) {
    root(PhotoScreen(activity, this, item, items))
  }
}
