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

abstract class ScreenRouter(
  containerScreen: ContainerScreen,
  open val navigator: ScreenNavigator = ScreenNavigator(containerScreen)
) : Router() {
  val currentScreen
    get() = navigator.lastScreen

  open fun root(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.root(Replace(mark, it))
    }
  }

  open fun replace(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.replace(Replace(mark, it))
    }
  }

  open fun forward(mark: String, args: Any? = null) {
    instantiate(mark, args)?.let {
      super.forward(Forward(mark, it))
    }
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
    when (command) {
      is Forward -> navigator.forwardScreen(command.data)
      is Backward -> navigator.backwardScreen(command.data)
      is Replace -> navigator.replaceScreen(command.data)
      is Destroy -> navigator.destroyScreen(command.data)
    }
  }

  abstract fun instantiate(mark: String, args: Any? = null): Screen<*>?
}

open class Forward(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)
open class Backward(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)
open class Replace(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)
open class Destroy(mark: String, screen: Screen<*>) : Command<Screen<*>>(mark, screen)