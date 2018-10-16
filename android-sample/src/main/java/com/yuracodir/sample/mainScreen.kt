package com.yuracodir.sample

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.estudio.magic.android.AndroidScreen
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_COLOR
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_FORWARD
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_PAGER
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_REPLACE
import com.yuracodir.sample.ActivityRouter.Companion.SCREEN_ROOT
import kotlinx.android.synthetic.main.item_menu.view.*
import kotlinx.android.synthetic.main.screen_main.view.*

class MainScreen(context: Context, router: ActivityRouter) :
  AndroidScreen<ActivityRouter>(context, router),
  MenuItemClickListener {

  companion object {
    const val MENU_ROOT = 0
    const val MENU_FORWARD = 1
    const val MENU_REPLACE = 2
    const val MENU_BACK = 3
    const val MENU_BACK_TO = 4
    const val MENU_COLOR = 5
    const val MENU_PAGER = 6
  }

  private var color: Int = 0
    set(value) {
      field = value
      root.color.setBackgroundColor(value)
    }
  override val layoutId = R.layout.screen_main
  private val adapter = MenuAdapter(this)

  override fun bind(view: View) {
    view.list.adapter = adapter
    view.list.layoutManager = LinearLayoutManager(context)
    adapter.setItems(
      listOf(
        MenuItem(
          MENU_ROOT,
          context.getString(R.string.main_root),
          context.getString(R.string.main_root_desc)
        ),
        MenuItem(
          MENU_FORWARD,
          context.getString(R.string.main_forward),
          context.getString(R.string.main_forward_desc)
        ),
        MenuItem(
          MENU_REPLACE,
          context.getString(R.string.main_replace),
          context.getString(R.string.main_replace_desc)
        ),
        MenuItem(
          MENU_BACK,
          context.getString(R.string.main_back),
          context.getString(R.string.main_back_desc)
        ),
        MenuItem(
          MENU_BACK_TO,
          context.getString(R.string.main_back_to),
          context.getString(R.string.main_back_to_desc)
        ),
        MenuItem(
          MENU_COLOR,
          context.getString(R.string.main_color),
          context.getString(R.string.main_color_desc)
        ),
        MenuItem(
          MENU_PAGER,
          context.getString(R.string.main_pager),
          context.getString(R.string.main_pager_desc)
        )
      )
    )
  }

  override fun create() {
    super.create()
    color = randomColor()
  }

  override fun resume() {
    super.resume()
    root.path.text = router.toString()
  }

  override fun onItemClick(item: MenuItem, position: Int) {
    when (item.id) {
      MENU_ROOT -> router.root(SCREEN_ROOT)
      MENU_FORWARD -> router.forward(SCREEN_FORWARD)
      MENU_REPLACE -> router.replace(SCREEN_REPLACE)
      MENU_BACK -> router.back()
      MENU_BACK_TO -> router.back(SCREEN_REPLACE)
      MENU_COLOR -> router.forward(SCREEN_COLOR, ColorArguments(color) {
        this.color = it
      })
      MENU_PAGER -> router.forward(SCREEN_PAGER, true)
    }
  }

  private fun randomColor(): Int {
    val randomColor: () -> Int = { (Math.random() * 255).toInt() }
    return Color.rgb(randomColor(), randomColor(), randomColor())
  }
}

class MenuAdapter(private val listener: MenuItemClickListener) :
  RecyclerView.Adapter<MenuViewHolder>() {
  private val items = arrayListOf<MenuItem>()

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
    MenuViewHolder(inflate(p0, R.layout.item_menu), listener)

  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
    holder.actual = items[holder.adapterPosition]
  }

  private fun inflate(parent: ViewGroup, res: Int) =
    LayoutInflater.from(parent.context).inflate(res, parent, false)

  fun setItems(items: List<MenuItem>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}

class MenuViewHolder(view: View, listener: MenuItemClickListener) : RecyclerView.ViewHolder(view) {

  init {
    itemView.setOnClickListener {
      val item = actual
      if (item != null) {
        listener.onItemClick(item, adapterPosition)
      }
    }
  }

  internal var actual: MenuItem? = null
    set(value) {
      field = value
      itemView.title.text = value?.title
      itemView.description.text = value?.desc
    }
}

interface MenuItemClickListener {
  fun onItemClick(item: MenuItem, position: Int)
}

class MenuItem(val id: Int, val title: String, val desc: String)

class ColorArguments(val color: Int, val result: (Int) -> Unit)