package com.estudio.magic


interface ContainerScreen {
  val childRouter: ScreenRouter
  fun attach(screen: Screen<*>)
  fun detach(screen: Screen<*>)
}

interface Screen<R : Router> {
  var router: R

  fun create()
  fun pause()
  fun resume()
  fun destroy()
  fun onBack(): Boolean // is Handled ?
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
      containerScreen.detach(it)
    }
    lastScreen = screen
    create(screen)
    containerScreen.attach(screen)
    resume(screen)
  }

  open fun backScreen(screen: Screen<*>) {
    lastScreen?.let {
      pause(it)
      containerScreen.detach(it)
      destroy(it)
    }
    lastScreen = screen
    create(screen)
    containerScreen.attach(screen)
    resume(screen)
  }

  fun create(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == null) {
        screenStates[it] = ScreenState.CREATED
        it.create()
      }
    }
  }

  fun resume(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.CREATED || state == ScreenState.PAUSED) {
        screenStates[it] = ScreenState.RESUMED
        it.resume()
      }
    }
  }

  fun pause(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.RESUMED) {
        screenStates[it] = ScreenState.PAUSED
        it.pause()
      }
    }
  }

  fun destroy(screen: Screen<*>? = lastScreen) {
    screen?.let {
      val state = screenStates[it]
      if (state == ScreenState.PAUSED || state == ScreenState.CREATED) {
        screenStates.remove(it)
        it.destroy()
      }
    }
  }
}

abstract class ScreenRouter(containerScreen: ContainerScreen) : Router() {
  open val navigator = ScreenNavigator(containerScreen)
  val currentScreen
    get() = navigator.lastScreen

  open fun root(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.root(Forward(mark, it))
    }
  }

  open fun replace(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.replace(Forward(mark, it))
    }
  }

  open fun forward(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.forward(Forward(mark, it))
    }
  }

  open fun back(mark: String? = null): Boolean {
    var command: Command<*>? = null

    if (history.size > 1) {
      if (mark == null) {
        command = history[history.size - 2]
      } else {
        while (history.size > 1) {
          val age = history[history.size - 2]
          command = age
          if (age.mark == mark) {
            break
          }
          if (history.size > 2) {
            history.pop()
          } else {
            break
          }
        }
      }
    }
    return command?.let {
      back(Back(it.mark, it.data as Screen<*>))
    } == true
  }

  override fun navigateTo(command: Command<*>) {
    when (command) {
      is Forward -> navigator.forwardScreen(command.data)
      is Back -> navigator.backScreen(command.data)
    }
  }

  abstract fun instantiate(mark: String, args: Any? = null): Screen<*>?
}

class Forward(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)
class Back(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)