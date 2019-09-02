package com.yuracodir.sample.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.yuracodir.magic.android.AndroidScreen
import com.yuracodir.sample.*
import com.yuracodir.sample.data.models.AlbumDto
import com.yuracodir.sample.data.models.PhotoDto
import com.yuracodir.sample.data.network.TypicodeApi
import kotlinx.android.synthetic.main.item_photo.view.*
import kotlinx.android.synthetic.main.screen_list.view.*

class PhotoGridScreen(context: Context, router: ActivityRouter, args: AlbumDto) :
    AndroidScreen<ActivityRouter>(context, router), SearchView.OnQueryTextListener {
  override val root = inflate(context, R.layout.screen_list)
  private val presenter = PhotoGridPresenter(this, router, args)
  private val adapter = PhotoGridAdapter(presenter)

  override fun create() {
    super.create()
    root.list.let {
      it.adapter = adapter
      it.layoutManager = GridLayoutManager(context, 3)
    }
    root.toolbar.let {
      it.setNavigationIcon(R.drawable.ic_arrow_back)
      it.setNavigationOnClickListener { presenter.onBack() }
      it.inflateMenu(R.menu.menu_search)
      val searchView = it.findViewById<SearchView>(R.id.search)
      searchView.setOnQueryTextListener(this)
    }
    presenter.onCreate()
  }

  override fun onQueryTextSubmit(p0: String): Boolean {
    return true
  }

  override fun onQueryTextChange(p0: String): Boolean {
    presenter.onSearchInput(p0)
    return true
  }

  fun setItems(items: List<PhotoDto>) {
    adapter.setItems(items)
  }

  fun showEmpty() {
    root.empty.visibility = View.VISIBLE
    root.list.visibility = View.GONE
  }

  fun hideEmpty() {
    root.empty.visibility = View.GONE
    root.list.visibility = View.VISIBLE
  }

  fun setTitle(title: String) {
    root.toolbar.title = title
  }
}

class PhotoGridAdapter(private val presenter: PhotoGridPresenter) : RecyclerView.Adapter<PhotoGridViewHolder>() {
  private val items: MutableList<PhotoDto> = mutableListOf()

  init {
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        presenter.onItemsChange(itemCount)
      }
    })
  }
  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PhotoGridViewHolder {
    return PhotoGridViewHolder(p0, presenter)
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(p0: PhotoGridViewHolder, p1: Int) {
    p0.bind(items[p1])
  }

  fun setItems(items: List<PhotoDto>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}

class PhotoGridViewHolder(
  parent: ViewGroup,
  private val presenter: PhotoGridPresenter) : RecyclerView.ViewHolder(inflate(parent, R.layout.item_photo)) {
  private lateinit var data: PhotoDto

  init {
    itemView.setOnClickListener { presenter.onPhotoClick(data) }
  }

  fun bind(item: PhotoDto) {
    data = item
    itemView.title.text = item.title
    itemView.image.load(item.thumbnailUrl) {
      placeholder(R.drawable.ic_image_holder)
      error(R.drawable.ic_image_holder)
    }
  }
}

class PhotoGridPresenter(
  private val view: PhotoGridScreen,
  private val router: ActivityRouter,
  private val args: AlbumDto) {
  private val service: TypicodeApi by inject()
  private val sourceItems = arrayListOf<PhotoDto>()

  fun onCreate() {
    view.setTitle(args.title)
    service.getPhotos(args.id).request { resp, err ->
      err?.let {
        view.setItems(emptyList())
      }
      resp?.let {
        sourceItems.clear()
        sourceItems.addAll(it)
        view.setItems(sourceItems)
      }
    }
  }

  fun onPhotoClick(data: PhotoDto) {
    router.startPhotoScreen(data, sourceItems)
  }

  fun onSearchInput(query: String) {
    view.setItems(sourceItems.filter { it.title.contains(query, true) })
  }

  fun onBack() {
    view.onBack()
  }

  fun onItemsChange(itemCount: Int) {
    if (itemCount == 0) {
      view.showEmpty()
    } else {
      view.hideEmpty()
    }
  }
}
