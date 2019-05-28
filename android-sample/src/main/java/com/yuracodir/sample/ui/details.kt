package com.yuracodir.sample.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.text.HtmlCompat
import android.view.View
import android.widget.Toast
import com.estudio.magic.android.AndroidScreen
import com.squareup.picasso.Picasso
import com.yuracodir.sample.*
import com.yuracodir.sample.data.models.VacancyDetailsDto
import com.yuracodir.sample.data.models.VacancyPreviewDto
import com.yuracodir.sample.data.network.HeadHunterApi
import kotlinx.android.synthetic.main.screen_details.view.*

class DetailsScreen(context: Context, router: ActivityRouter, args: VacancyPreviewDto) :
    AndroidScreen<ActivityRouter>(context, router) {

  override val root = inflate(context, R.layout.screen_details)
  private val presenter = DetailsPresenter(this, router, args)

  override fun create() {
    super.create()
    root.link.setOnClickListener {
      presenter.onLinkClick()
    }
    root.toolbar.setNavigationOnClickListener {
      onBack()
    }
    presenter.onCreate()
  }

  fun setTitle(name: String) {
    root.toolbar.title = name
  }

  fun setDescription(description: String) {
    root.description.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
  }

  fun setAddress(address: String?) {
    root.address.text = address
  }

  fun setSalary(salary: String?) {
    root.salary.text = salary
  }

  fun setImage(url: String?) {
    Picasso
        .get()
        .load(url)
        .placeholder(R.drawable.ic_image_error)
        .error(R.drawable.ic_image_error)
        .into(root.image)
  }

  fun showErrorLoad() {
    Toast.makeText(context, R.string.details_error_load, Toast.LENGTH_LONG).show()
  }

  fun showButtonLink() {
    root.link_layout.visibility = View.VISIBLE
  }

  fun hideButtonLink() {
    root.link_layout.visibility = View.GONE
  }

  fun openLink(title: String, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
      context.startActivity(Intent.createChooser(intent, title))
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

class DetailsPresenter(
  private val view: DetailsScreen,
  private val router: ActivityRouter,
  private val args: VacancyPreviewDto) {
  private val serviceApi: HeadHunterApi by inject()
  private var vacancy: VacancyDetailsDto? = null

  fun onCreate() {
    //setup preview data
    view.setTitle(args.name)
    view.setImage(args.employer.logo?.original)
    view.setAddress(args.address.toString())
    view.setSalary(args.salary.toString())
    //get full data
    serviceApi.getVacancyDetails(args.id).request { vacancyDetails, throwable ->
      vacancyDetails?.let {
        this.vacancy = it
        view.setTitle(it.name)
        view.setImage(it.employer.logo?.original)
        view.setDescription(it.description)
        view.setAddress(it.address?.toString())
        view.setSalary(it.salary?.toString())
      }
      throwable?.let {
        view.showErrorLoad()
      }
      if (vacancy?.url != null) {
        view.showButtonLink()
      } else {
        view.hideButtonLink()
      }
    }
  }

  fun onLinkClick() {
    vacancy?.let {
      view.openLink(it.name, it.url)
    }
  }
}