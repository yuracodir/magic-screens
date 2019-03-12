package com.yuracodir.sample.widgets

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class PaginationScrollListener(private val listener: PaginationListener) :
    RecyclerView.OnScrollListener() {
  var isLoading = false
  var maxPages = 0
  var perPage: Int = 20

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    val layoutManager = recyclerView.layoutManager ?: return
    if (layoutManager is LinearLayoutManager) {
      val visibleItemCount = layoutManager.childCount
      val totalItemCount = layoutManager.itemCount
      val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
      val page = (recyclerView.adapter?.itemCount ?: 0) / perPage
      if (!isLoading && page < maxPages) {
        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
          listener.loadMoreItems(page, perPage)
        }
      }
    }
  }
}

interface PaginationListener {
  fun loadMoreItems(page: Int, perPage: Int)
}