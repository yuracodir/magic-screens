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
  fun back(): Boolean // is Handled ?
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
    }.let {
      applyCommand(*it.toTypedArray())
    }
  }

  open fun root(screen: Screen<*>) {
    applyCommand(Root(screen.getName(), screen))
  }

  open fun replace(screen: Screen<*>) {
    applyCommand(Replace(screen.getName(), screen))
  }

  open fun forward(screen: Screen<*>) {
    applyCommand(Forward(screen.getName(), screen))
  }

  open fun back(mark: String? = null): Boolean {
    if (history.size > 1) {
      if (mark == null) {
        val command = history[history.size - 2]
        applyCommand(Backward(command.mark, command.data as Screen<*>))
        return true
      } else {
        while (history.size > 1) {
          val command = history[history.size - 2]
          if (command.mark == mark || history.size == 2) {
            applyCommand(Backward(command.mark, command.data as Screen<*>))
            return true
          }
          applyCommand(Destroy(command.mark, command.data as Screen<*>))
        }
      }
    }
    return false
  }

  override fun applyCommand(vararg command: Command<*>) {
    val lastCommand = command.lastOrNull() ?: return
    val data = lastCommand.data
    super.applyCommand(*command)
    if (data is Screen<*>) {
      when (lastCommand) {
        is Forward -> navigator.forwardScreen(data)
        is Backward -> navigator.backwardScreen(data)
        is Root, is Replace -> navigator.replaceScreen(data)
        is Destroy -> navigator.destroyScreen(data)
      }
    }
  }
}

open class Forward(override val mark: String, override val data: Any) : Command<Any> {
  override fun applyCommand(history: MutableList<Command<*>>) {
    history.add(this)
  }
}

open class Backward(override val mark: String, override val data: Any) : Command<Any> {
  override fun applyCommand(history: MutableList<Command<*>>) {
    history.removeAt(history.lastIndex)
  }
}

open class Replace(override val mark: String, override val data: Any) : Command<Any> {
  override fun applyCommand(history: MutableList<Command<*>>) {
    history[history.lastIndex] = this
  }
}

open class Root(override val mark: String, override val data: Any) : Command<Any> {
  override fun applyCommand(history: MutableList<Command<*>>) {
    history.clear()
    history.add(this)
  }
}

open class Destroy(override val mark: String, override val data: Any) : Command<Any> {
  override fun applyCommand(history: MutableList<Command<*>>) {
    history.removeAt(history.lastIndex)
  }
}