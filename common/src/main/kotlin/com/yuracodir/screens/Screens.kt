package com.yuracodir.screens

interface ContainerScreen {
  val childRouter: ScreenRouter
  fun attach(screen: Screen<*>)
  fun detach(screen: Screen<*>)
}

interface ScreenCallback {
  fun onCreate()
  fun onPause()
  fun onResume()
  fun onDestroy()
  fun onBack(): Boolean
}

interface Screen<R : Router> {
  var router: R
  fun create()
  fun resume()
  fun pause()
  fun destroy()
  fun onBack(): Boolean // is Handled ?
  fun getName(): String = ""
}

open class ScreenNavigator(private val containerScreen: ContainerScreen) {
  private enum class ScreenState {
    CREATED,
    RESUMED,
    PAUSED
  }

  private var screenStates = HashMap<Screen<*>, ScreenState?>()
  var lastScreen: Screen<*>? = null

  open fun forwardScreen(screen: Screen<*>) {
    lastScreen?.let {
      pause(it)
      detach(it)
    }
    lastScreen = screen
    create(screen)
    attach(screen)
    resume(screen)
  }

  open fun backwardScreen(screen: Screen<*>) {
    lastScreen?.let {
      pause(it)
      detach(it)
      destroy(it)
    }
    lastScreen = screen
    create(screen)
    attach(screen)
    resume(screen)
  }

  open fun replaceScreen(screen: Screen<*>) {
    lastScreen?.let {
      pause(it)
      detach(it)
      destroy(it)
    }
    lastScreen = screen
    create(screen)
    attach(screen)
    resume(screen)
  }

  open fun destroyScreen(screen: Screen<*>) {
    screen.let {
      pause(it)
      destroy(it)
    }
  }

  open fun detach(screen: Screen<*>) {
    containerScreen.detach(screen)
  }

  open fun attach(screen: Screen<*>) {
    containerScreen.attach(screen)
  }

  open fun create(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == null) {
        screenStates[it] = ScreenState.CREATED
        it.create()
      }
    }
  }

  open fun resume(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.CREATED || state == ScreenState.PAUSED) {
        screenStates[it] = ScreenState.RESUMED
        it.resume()
      }
    }
  }

  open fun pause(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.RESUMED) {
        screenStates[it] = ScreenState.PAUSED
        it.pause()
      }
    }
  }

  open fun destroy(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.PAUSED || state == ScreenState.CREATED) {
        screenStates.remove(it)
        it.destroy()
      }
    }
  }
}

open class ScreenRouter(
  containerScreen: ContainerScreen,
  open val navigator: ScreenNavigator = ScreenNavigator(containerScreen)
) : Router() {
  val currentScreen
    get() = navigator.lastScreen

  open fun chain(vararg screens: Screen<*>) {
    screens.map { screen ->
      Replace(screen.getName(), screen)
    }.toTypedArray().let {
      super.chain(*it)
    }
  }

  open fun root(screen: Screen<*>) {
    super.root(Replace(screen.getName(), screen))
  }

  open fun replace(screen: Screen<*>) {
    super.replace(Replace(screen.getName(), screen))
  }

  open fun forward(screen: Screen<*>) {
    super.forward(Forward(screen.getName(), screen))
  }

  open fun back(mark: String? = null): Boolean {
    if (history.size > 1) {
      if (mark == null) {
        val command = history[history.size - 2]
        super.back(Backward(command.mark, command.data as Screen<*>))
        return true
      } else {
        while (history.size > 1) {
          val command = history[history.size - 2]
          if (command.mark == mark || history.size == 2) {
            super.back(Backward(command.mark, command.data as Screen<*>))
            return true
          }
          super.back(Destroy(command.mark, command.data as Screen<*>))
        }
      }
    }
    return false
  }

  override fun navigateTo(command: Command<*>) {
    val data = command.data
    if (data is Screen<*>) {
      when (command) {
        is Forward -> navigator.forwardScreen(data)
        is Backward -> navigator.backwardScreen(data)
        is Replace -> navigator.replaceScreen(data)
        is Destroy -> navigator.destroyScreen(data)
      }
    }
  }
}

open class Forward(mark: String, screen: Any) : Command<Any>(mark, screen)
open class Backward(mark: String, screen: Any) : Command<Any>(mark, screen)
open class Replace(mark: String, screen: Any) : Command<Any>(mark, screen)
open class Destroy(mark: String, screen: Any) : Command<Any>(mark, screen)