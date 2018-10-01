package com.yuracodir.android_sample.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.estudio.magic.Router
import com.estudio.magic.Screen
import com.estudio.magic.ScreenLifecycle
import com.estudio.magic.android.AndroidContainerScreen
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.android_sample.R
import kotlinx.android.synthetic.main.item_screen.view.*
import kotlinx.android.synthetic.main.screen_pager.view.*


abstract class PagerScreen<Ro : Router, A : Any>(context: Context, router: Ro) :
  AndroidContainerScreen<Ro, A>(context, router),
  OnScreenPageChanged {

  abstract override val childRouter: PagerRouter
  private var lastPosition = 0
  abstract val screens: Array<String>

  protected val adapter = ScreenContainerAdapter(router.lifecycle)
  internal val layoutManager: RecyclerView.LayoutManager =
    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
  private lateinit var list: RecyclerView
  private lateinit var androidScreens: Array<AndroidScreen<*, *>>

  override fun bind(view: View) {
    list = view.list_container
    list.adapter = adapter
    list.layoutManager = layoutManager
    list.addOnScrollListener(PagerScrollListener(this))
    adapter.setItems(androidScreens)
    PagerSnapHelper().attachToRecyclerView(list)
  }

  override fun create() {
    val list = arrayListOf<AndroidScreen<*, *>>()
    val screens = this.screens
    screens.forEachIndexed { index, mark ->
      val screen = instantiate(mark)
      if (screen != null && screen is AndroidScreen<*, *>) {
        list.add(screen)
      }
    }
    androidScreens = list.toTypedArray()
    super.create()
    androidScreens.forEach { childRouter.lifecycle.create(it) }
  }

  override fun pause() {
    super.pause()
    childRouter.lifecycle.pause(androidScreens[lastPosition])
  }

  override fun resume() {
    super.resume()
    childRouter.lifecycle.resume(androidScreens[lastPosition])
  }

  override fun destroy() {
    super.destroy()
    androidScreens.forEach { childRouter.lifecycle.destroy(it) }
  }

  fun show(position: Int) {
    list.smoothScrollToPosition(position)
  }

  fun show(mark: String, args: Any? = null) {
    val page = screens.indexOf(mark)
    if (args != null) {
      (androidScreens[page] as Screen<*, Any>).args = args
    }
    show(page)
  }

  override fun onPageChanged(position: Int) {
    if (position != lastPosition) {
      val oldScreen = androidScreens[lastPosition]
      val newScreen = androidScreens[position]
      lastPosition = position
      router.lifecycle.pause(oldScreen)
      router.lifecycle.resume(newScreen)
    }
  }

  override fun attach(screen: Screen<*, *>) {}

  override fun detach(screen: Screen<*, *>) {}
}

open class PagerRouter(val containerScreen: PagerScreen<*, *>) : Router(containerScreen) {
  override fun forward(mark: String, args: Any?) {
    containerScreen.show(mark, args)
  }

  override fun replace(mark: String, args: Any?) {

  }

  override fun back(mark: String?, args: Any?): Boolean {
    return containerScreen.router.back(mark, args)
  }

  override fun root(mark: String, args: Any?) {

  }

}

class PagerScrollListener(private val listener: OnScreenPageChanged) :
  RecyclerView.OnScrollListener() {
  private var position: Int = -1
  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    if (recyclerView.layoutManager is LinearLayoutManager) {
      val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

      val position = if (dx > 0) {
        linearLayoutManager.findLastVisibleItemPosition()
      } else {
        linearLayoutManager.findFirstVisibleItemPosition()
      }

      if (position >= 0 && position != this.position && position < (recyclerView.adapter?.itemCount ?: 1) - 1) {
        this.position = position
        listener.onPageChanged(this.position)
      }
    }
    super.onScrolled(recyclerView, dx, dy)
  }
}

interface OnScreenPageChanged {
  fun onPageChanged(position: Int)
}

class ScreenContainerAdapter(private val lifecycle: ScreenLifecycle) :
  RecyclerView.Adapter<ScreenViewHolder<AndroidScreen<*, *>>>() {

  private val items = mutableListOf<AndroidScreen<*, *>>()

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ScreenViewHolder<AndroidScreen<*, *>> {
    return ScreenViewHolder(
      lifecycle,
      LayoutInflater
        .from(parent.context)
        .inflate(R.layout.item_screen, parent, false)
    )
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: ScreenViewHolder<AndroidScreen<*, *>>, position: Int) {
    val screen = items[holder.adapterPosition]
    holder.update(screen)
  }

  fun setItems(screens: Array<AndroidScreen<*, *>>) {
    items.clear()
    items.addAll(screens)
    notifyDataSetChanged()
  }

  fun add(screen: AndroidScreen<*, *>) {
    items.add(screen)
    notifyItemRangeInserted(itemCount, 1)
  }
}

class ScreenViewHolder<S : AndroidScreen<*, *>>(private val lifecycle: ScreenLifecycle, view: View) :
  RecyclerView.ViewHolder(view) {
  private var lastItem: S? = null
  private val container = view.item_container
  fun update(item: S) {
    val last = lastItem
    if (last != null) {
      container.removeView(last.root)
      lifecycle.pause(last)
    }
    container.addView(item.root)
    lifecycle.resume(item)
    lastItem = item
  }
}
