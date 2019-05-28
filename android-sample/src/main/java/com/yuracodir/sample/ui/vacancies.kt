package com.yuracodir.sample.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.ViewGroup
import com.estudio.magic.android.AndroidScreen
import com.squareup.picasso.Picasso
import com.yuracodir.sample.*
import com.yuracodir.sample.data.models.VacancyPreviewDto
import com.yuracodir.sample.data.network.HeadHunterApi
import com.yuracodir.sample.widgets.PaginationListener
import com.yuracodir.sample.widgets.PaginationScrollListener
import kotlinx.android.synthetic.main.item_vacancy.view.*
import kotlinx.android.synthetic.main.screen_main.view.*

class VacanciesScreen(context: Context, router: ActivityRouter) :
    AndroidScreen<ActivityRouter>(context, router), SearchView.OnQueryTextListener, PaginationListener {

  override val root = inflate(context, R.layout.screen_main)
  private val presenter = VacanciesPresenter(this, router)
  private val adapter = VacanciesAdapter(presenter)
  private val pagination = PaginationScrollListener(this)

  override fun create() {
    super.create()
    root.list.adapter = adapter
    root.toolbar.inflateMenu(R.menu.menu_search)
    root.list.addOnScrollListener(pagination)
    val searchView = root.toolbar.findViewById<SearchView>(R.id.search)
    searchView.setOnQueryTextListener(this)
    presenter.getVacanciesList(searchView.query.toString())
  }

  override fun onQueryTextSubmit(p0: String): Boolean {
    return true
  }

  override fun onQueryTextChange(p0: String): Boolean {
    presenter.getVacanciesList(p0)
    return true
  }

  override fun loadMoreItems(page: Int, perPage: Int) {
    presenter.onPaginationLoad(page, perPage)
    pagination.isLoading = true
  }

  fun setItems(items: List<VacancyPreviewDto>) {
    adapter.setItems(items)
    pagination.isLoading = false
  }

  fun addItems(items: List<VacancyPreviewDto>) {
    adapter.addItems(items)
    pagination.isLoading = false
  }

  fun setPages(perPage: Int, pages: Int) {
    pagination.perPage = perPage
    pagination.maxPages = pages
  }
}

class VacanciesAdapter(private val presenter: VacanciesPresenter) : RecyclerView.Adapter<VacancyViewHolder>() {
  private val items: MutableList<VacancyPreviewDto> = mutableListOf()

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VacancyViewHolder {
    return VacancyViewHolder(p0, presenter)
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(p0: VacancyViewHolder, p1: Int) {
    p0.bind(items[p1])
  }

  fun addItems(items: List<VacancyPreviewDto>) {
    this.items.addAll(items)
    notifyDataSetChanged()
  }

  fun setItems(items: List<VacancyPreviewDto>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}

class VacancyViewHolder(
  parent: ViewGroup,
  private val presenter: VacanciesPresenter) : RecyclerView.ViewHolder(inflate(parent, R.layout.item_vacancy)) {
  private lateinit var data: VacancyPreviewDto

  init {
    itemView.setOnClickListener { presenter.onVacancyClick(data) }
  }

  fun bind(vacancy: VacancyPreviewDto) {
    data = vacancy
    itemView.name.text = vacancy.name
    itemView.address.text = vacancy.address?.toString()
    itemView.salary.text = vacancy.salary?.toString()
    Picasso
        .get()
        .load(vacancy.employer.logo?.medium)
        .placeholder(R.drawable.ic_image_error)
        .error(R.drawable.ic_image_error)
        .into(itemView.image)
  }
}

class VacanciesPresenter(
  private val view: VacanciesScreen,
  private val router: ActivityRouter) {
  private val service: HeadHunterApi by inject()
  private var query = ""
  private val perPage = 20

  fun onVacancyClick(data: VacancyPreviewDto) {
    router.startDetailsScreen(data)
  }

  fun getVacanciesList(query: String) {
    this.query = query
    service.getVacancies(query, 0, perPage).request { resp, err ->
      resp?.items?.let {
        view.setItems(it)
      }
      view.setPages(perPage, resp?.pages ?: 0)
    }
  }

  fun onPaginationLoad(page: Int, perPage: Int) {
    service.getVacancies(query, page, perPage).request { resp, err ->
      resp?.items?.let {
        view.addItems(it)
      }
    }
  }
}
