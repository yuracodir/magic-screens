package com.yuracodir.sample.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.estudio.magic.*
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.sample.R
import kotlinx.android.synthetic.main.item_screen.view.*
import kotlinx.android.synthetic.main.screen_pager.view.*


abstract class PagerScreen<Ro : ScreenRouter>(context: Context, router: Ro) :
  AndroidScreen<Ro>(context, router),
  PagerScreenContainer, OnPagerScreenChanged {

  abstract override val childRouter: PagerRouter
  private var lastPosition = 0
  abstract val screens: Array<String>

  protected val adapter = ScreenContainerAdapter()
  internal val layoutManager: RecyclerView.LayoutManager =
    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
  private lateinit var list: RecyclerView

  override fun bind(view: View) {
    list = view.list_container
    list.adapter = adapter
    list.layoutManager = layoutManager
    list.addOnScrollListener(PagerScrollListener(this))
    PagerSnapHelper().attachToRecyclerView(list)
  }

  override fun create() {
    super.create()
    val screens = this.screens
    screens.forEach { mark ->
      childRouter.instantiate(mark, null)
    }
    childRouter.navigator.create()
  }

  override fun pause() {
    super.pause()
    childRouter.navigator.pause()
  }

  override fun resume() {
    super.resume()
    childRouter.navigator.resume()
  }

  override fun destroy() {
    super.destroy()
    childRouter.navigator.destroy()
  }

  override fun scrollTo(position: Int) {
    list.smoothScrollToPosition(position)
  }

  override fun setItems(screens: Array<AndroidScreen<*>>) {
    adapter.setItems(screens)
  }

  abstract fun instantiate(mark: String, args: Any?): Screen<*>?

  override fun onPageChanged(position: Int) {
    childRouter.navigator.onPageChanged(position)
  }
}

abstract class PagerRouter(private val containerScreen: PagerScreen<*>) : Router() {

  val navigator = PagerPagerScreenNavigator(containerScreen)

  fun instantiate(mark: String, args: Any?): Screen<*>? {
    val screen = navigator.getScreenByMark(mark)
    if (screen == null) {
      containerScreen.instantiate(mark, args)?.let {
        if (it is AndroidScreen<*>) {
          navigator.screens.add(mark to it)
        }
      }
    }
    return screen
  }

  fun show(mark: String, args: Any? = null) {
    val screen = instantiate(mark, args)
    screen?.let {
      if (isEmpty()) {
        super.root(Forward(mark, it))
      } else {
        super.replace(Forward(mark, it))
      }
    }
  }

  override fun navigateTo(command: Command<*>) {
    when (command) {
      is Forward -> navigator.show(command.data)
    }
  }
}

interface PagerScreenContainer {
  val childRouter: PagerRouter
  fun scrollTo(position: Int)
  fun setItems(screens: Array<AndroidScreen<*>>)
}

class PagerPagerScreenNavigator(val container: PagerScreenContainer) : OnPagerScreenChanged {

  private enum class ScreenState {
    NONE,
    CREATED,
    RESUMED,
    PAUSED,
    DESTROYED,
  }

  private var screenStates = HashMap<Screen<*>, ScreenState?>()
  var lastScreen: Screen<*>? = null
  var screens = arrayListOf<Pair<String, AndroidScreen<*>>>()

  private var lastPosition = -1

  override fun onPageChanged(position: Int) {
    if (position != lastPosition) {
      val oldScreen = lastScreen
      val newScreen = getScreenByPosition(position)

      lastPosition = position
      lastScreen = newScreen
      pause(oldScreen)
      resume(newScreen)
    }
  }

  fun show(screen: Screen<*>) {
    container.scrollTo(getPositionByScreen(screen))
  }

  fun getScreenByPosition(position: Int): Screen<*>? {
    return screens[position].second
  }

  fun getScreenByMark(mark: String): Screen<*>? {
    screens.forEach { pair ->
      if (pair.first == mark) {
        return pair.second
      }
    }
    return null
  }

  fun getPositionByScreen(screen: Screen<*>): Int {
    screens.forEachIndexed { index, pair ->
      if (pair.second == screen) {
        return index
      }
    }
    return -1
  }

  fun create() {
    val screensArray = arrayListOf<AndroidScreen<*>>()
    screens.forEachIndexed { index, it ->
      val screen = it.second
      val state = screenStates[screen]
      if (state == null) {
        screenStates[screen] = ScreenState.CREATED
        screen.create()
      }
      screensArray.add(screen)
    }
    container.setItems(screensArray.toTypedArray())
  }

  fun resume(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.CREATED || state == ScreenState.PAUSED) {
        screenStates[it] = ScreenState.RESUMED
        it.resume()
      }
    }
  }

  fun pause(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.RESUMED) {
        screenStates[it] = ScreenState.PAUSED
        it.pause()
      }
    }
  }

  fun destroy() {
    screens.forEach {
      val screen = it.second
      val state = screenStates[screen]
      if (state == ScreenState.PAUSED || state == ScreenState.CREATED) {
        screenStates.remove(screen)
        screen.destroy()
      }
    }
  }
}

class PagerScrollListener(private val listener: OnPagerScreenChanged) :
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

      if (position >= 0 && position != this.position && position < (recyclerView.adapter?.itemCount ?: 1)) {
        this.position = position
        listener.onPageChanged(this.position)
      }
    }
    super.onScrolled(recyclerView, dx, dy)
  }
}

interface OnPagerScreenChanged {
  fun onPageChanged(position: Int)
}

class ScreenContainerAdapter : RecyclerView.Adapter<ScreenViewHolder<AndroidScreen<*>>>() {

  private val items = mutableListOf<AndroidScreen<*>>()

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ScreenViewHolder<AndroidScreen<*>> {
    return ScreenViewHolder(
      LayoutInflater
        .from(parent.context)
        .inflate(R.layout.item_screen, parent, false)
    )
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: ScreenViewHolder<AndroidScreen<*>>, position: Int) {
    val screen = items[holder.adapterPosition]
    holder.update(screen)
  }

  fun setItems(screens: Array<AndroidScreen<*>>) {
    items.clear()
    items.addAll(screens)
    notifyDataSetChanged()
  }

  fun add(screen: AndroidScreen<*>) {
    items.add(screen)
    notifyItemRangeInserted(itemCount, 1)
  }
}

class ScreenViewHolder<S : AndroidScreen<*>>(view: View) :
  RecyclerView.ViewHolder(view) {
  private var lastItem: S? = null
  private val container = view.item_container
  fun update(item: S) {
    val last = lastItem
    if (last != null) {
      container.removeView(last.root)
    }
    container.addView(item.root)
    lastItem = item
  }
}
