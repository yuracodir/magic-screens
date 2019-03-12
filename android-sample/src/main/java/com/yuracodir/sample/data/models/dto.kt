package com.yuracodir.sample.data.models

import com.google.gson.annotations.SerializedName
import com.yuracodir.sample.joinNotNull

class VacanciesResponse(val items: List<VacancyPreviewDto>, val pages: Int)

class VacancyPreviewDto(
  val id: Long,
  val name: String,
  val address: AddressDto?,
  val salary: SalaryDto?,
  @SerializedName("alternate_url")
  val url: String?,
  val employer: EmployerDto
)

class VacancyDetailsDto(
  val id: Long,
  val name: String,
  val description: String,
  val address: AddressDto?,
  val salary: SalaryDto?,
  @SerializedName("alternate_url")
  val url: String,
  val employer: EmployerDto,
  val employment: EmploymentDto,
  @SerializedName("key_skills")
  val skills: List<SkillDto>
)

class SkillDto(val name: String)

class EmploymentDto(val id: String, val name: String)

class AddressDto(
  val city: String?,
  val street: String?,
  val building: String?,
  val description: String
) {
  override fun toString(): String {
    return arrayOf(city, street, building).joinNotNull()
  }
}

class SalaryDto(
  val from: Int,
  val to: Int,
  val currency: String
) {
  override fun toString(): String {
    if (to > 0) {
      return "$from - $to $currency"
    }
    return "$from $currency"
  }
}

class EmployerDto(
  val id: Long,
  val name: String,
  @SerializedName("logo_urls")
  val logo: LogoDto?,
  @SerializedName("alternate_url")
  val url: String
)

class LogoDto(
  @SerializedName("90")
  val small: String,
  @SerializedName("240")
  val medium: String,
  val original: String
)