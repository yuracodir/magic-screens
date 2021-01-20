package com.yuracodir.sample.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.yuracodir.sample.R
import com.yuracodir.sample.data.models.PhotoDto
import com.yuracodir.sample.inflate
import com.yuracodir.screens.ScreenRouter
import com.yuracodir.screens.android.AndroidScreen
import kotlinx.android.synthetic.main.item_photo.view.*
import kotlinx.android.synthetic.main.screen_photo.view.*

class PhotoScreen(context: Context, router: ScreenRouter, item: PhotoDto, items: List<PhotoDto>) :
    AndroidScreen<ScreenRouter>(context, router) {
  override val root = inflate(context, R.layout.screen_photo)
  private val presenter = PhotoPresenter(this, router, item, items)
  private val adapter = PhotoAdapter(presenter)

  override fun create() {
    super.create()
    root.list.let {
      it.adapter = adapter
      it.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
      PagerSnapHelper().attachToRecyclerView(it)
    }
    root.toolbar.let {
      it.setNavigationIcon(R.drawable.ic_arrow_back)
      it.setNavigationOnClickListener { presenter.onBack() }
    }
    presenter.onCreate()
  }

  fun showItems(items: List<PhotoDto>) {
    adapter.setItems(items)
    root.empty.visibility = View.GONE
    root.list.visibility = View.VISIBLE
  }

  fun showEmptyList() {
    root.empty.visibility = View.VISIBLE
    root.list.visibility = View.GONE
  }

  fun setTitle(title: String) {
    root.toolbar.title = title
  }

  fun scrollToPosition(position: Int) {
    root.list.scrollToPosition(position)
  }
}

class PhotoAdapter(private val presenter: PhotoPresenter) : RecyclerView.Adapter<PhotoViewHolder>() {
  private val items: MutableList<PhotoDto> = mutableListOf()

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PhotoViewHolder {
    return PhotoViewHolder(p0, presenter)
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(p0: PhotoViewHolder, p1: Int) {
    p0.bind(items[p1])
  }

  fun setItems(items: List<PhotoDto>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}

class PhotoViewHolder(
  parent: ViewGroup,
  private val presenter: PhotoPresenter) : RecyclerView.ViewHolder(inflate(parent, R.layout.item_photo_page)) {
  private lateinit var data: PhotoDto

  fun bind(item: PhotoDto) {
    data = item
    itemView.image.load(item.url) {
      placeholder(R.drawable.ic_image_holder)
      error(R.drawable.ic_image_holder)
    }
  }
}

class PhotoPresenter(
  private val view: PhotoScreen,
  private val router: ScreenRouter,
  private val item: PhotoDto,
  private val items: List<PhotoDto>) {
  fun onCreate() {
    if (items.isEmpty()) {
      view.showEmptyList()
    } else {
      view.showItems(items)
      val position = items.indexOf(item)
      view.scrollToPosition(position)
    }
  }

  fun onBack() {
    view.back()
  }
}
