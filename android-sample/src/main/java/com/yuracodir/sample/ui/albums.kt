package com.yuracodir.sample.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuracodir.sample.*
import com.yuracodir.sample.data.models.AlbumDto
import com.yuracodir.sample.data.network.TypicodeApi
import com.yuracodir.screens.android.AndroidScreen
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.android.synthetic.main.screen_list.view.*

class AlbumListScreen(context: Context, router: ActivityRouter) :
    AndroidScreen<ActivityRouter>(context, router), SearchView.OnQueryTextListener {
  override val root = inflate(context, R.layout.screen_list)
  private val presenter = AlbumListPresenter(this, router)
  private val adapter = AlbumListAdapter(presenter)

  override fun create() {
    super.create()
    root.list.let {
      it.adapter = adapter
      it.layoutManager = LinearLayoutManager(context)
      it.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
        context.getDrawable(R.drawable.shape_divider)?.let { drawable ->
          setDrawable(drawable)
        }
      })
    }
    root.toolbar.let {
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

  fun showItems(items: List<AlbumDto>) {
    adapter.setItems(items)
    root.empty.visibility = View.GONE
    root.list.visibility = View.VISIBLE
  }

  fun showEmpty() {
    root.empty.visibility = View.VISIBLE
    root.list.visibility = View.GONE
  }

  fun hideEmpty() {
    root.empty.visibility = View.GONE
    root.list.visibility = View.VISIBLE
  }
}

class AlbumListAdapter(private val presenter: AlbumListPresenter) : RecyclerView.Adapter<AlbumViewHolder>() {
  private val items: MutableList<AlbumDto> = mutableListOf()

  init {
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        presenter.onItemsChange(itemCount)
      }
    })
  }

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumViewHolder {
    return AlbumViewHolder(p0, presenter)
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(p0: AlbumViewHolder, p1: Int) {
    p0.bind(items[p1])
  }

  fun setItems(items: List<AlbumDto>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}

class AlbumViewHolder(
  parent: ViewGroup,
  private val presenter: AlbumListPresenter) : RecyclerView.ViewHolder(inflate(parent, R.layout.item_album)) {
  private lateinit var data: AlbumDto

  init {
    itemView.setOnClickListener { presenter.onAlbumClick(data) }
  }

  fun bind(item: AlbumDto) {
    data = item
    itemView.name.text = item.title
  }
}

class AlbumListPresenter(
  private val view: AlbumListScreen,
  private val router: ActivityRouter) {
  private val service: TypicodeApi by inject()
  private val sourceItems = arrayListOf<AlbumDto>()

  fun onCreate() {
    service.getAlbums().request { resp, err ->
      err?.let {
        view.showItems(emptyList())
      }
      resp?.takeIf { it.isNotEmpty() }?.let {
        sourceItems.clear()
        sourceItems.addAll(it)
        view.showItems(sourceItems)
      }
    }
  }

  fun onAlbumClick(data: AlbumDto) {
    router.startDetailsScreen(data)
  }

  fun onSearchInput(query: String) {
    view.showItems(sourceItems.filter { it.title.contains(query, true) })
  }

  fun onItemsChange(itemCount: Int) {
    if (itemCount == 0) {
      view.showEmpty()
    } else {
      view.hideEmpty()
    }
  }
}
