package com.yuracodir.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.Scope
import org.koin.standalone.StandAloneContext
import retrofit2.Response

fun inflate(context: Context, res: Int): View = View.inflate(context, res, null)

fun inflate(view: ViewGroup, res: Int): View = LayoutInflater.from(view.context).inflate(res, view, false)

inline fun <reified T : Any> inject(
  name: String = "",
  scope: Scope? = null,
  noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { get<T>(name, scope, parameters) }

inline fun <reified T : Any> get(
  name: String = "",
  scope: Scope? = null,
  noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T = getKoin().get(name, scope, parameters)

fun getKoin(): KoinContext = StandAloneContext.getKoin().koinContext

fun <T> Deferred<Response<T>>.request(callback: (T?, Throwable?) -> Unit) {
  GlobalScope.launch(Dispatchers.Main) {
    try {
      val response = await()
      callback(response.body(), null)
    } catch (e: Throwable) {
      callback.invoke(null, e)
    }
  }
}

fun Array<String?>.joinNotNull(separator: CharSequence = ", "): String {
  var buffer = ""
  for (element in this) {
    if (element.isNullOrEmpty()) {
      continue
    }
    buffer += element + separator
  }
  return buffer.removeSuffix(separator)
}