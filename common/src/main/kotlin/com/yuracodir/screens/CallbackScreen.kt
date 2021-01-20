package com.yuracodir.screens


abstract class CallbackScreen<R : Router> : Screen<R> {
  private val lifecycleCallbacks = mutableListOf<ScreenCallback>()

  fun registerCallback(callbacks: ScreenCallback) {
    lifecycleCallbacks.add(callbacks)
  }

  fun unregisterCallback(callbacks: ScreenCallback) {
    lifecycleCallbacks.remove(callbacks)
  }

  override fun create() {
    lifecycleCallbacks.forEach { it.onCreate() }
  }

  override fun resume() {
    lifecycleCallbacks.forEach { it.onResume() }
  }

  override fun pause() {
    lifecycleCallbacks.forEach { it.onPause() }
  }

  override fun destroy() {
    lifecycleCallbacks.forEach { it.onDestroy() }
  }

  override fun back(): Boolean {
    return lifecycleCallbacks.any { it.onBack() }
  }
}