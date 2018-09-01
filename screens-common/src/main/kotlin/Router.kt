package com.estudio.magic

interface ContainerScreen {
  val childRouter: Router
  fun attach(screen: Screen<*, *>)
  fun detach(screen: Screen<*, *>)
  fun instantiate(mark: String): Screen<*, *>?
}

interface Screen<R : Router, A> {
  var args: A
  var state: ScreenState
  var router: R
  fun create() // do not navigate in same router here
  fun pause()
  fun resume()
  fun destroy()
  fun onBack(): Boolean // is Handled ?
}

enum class ScreenState {
  NONE,
  CREATED,
  RESUMED,
  PAUSED,
  DESTROYED,
}

open class Router(val container: ContainerScreen) {
  private val history: Stack<StackEntry<*>> = Stack()

  fun createScreen(screen: Screen<*, *>) {
    if (screen.state == ScreenState.NONE) {
      screen.state = ScreenState.CREATED
      screen.create()
    }
  }

  fun resumeScreen(screen: Screen<*, *>) {
    if (screen.state == ScreenState.CREATED || screen.state == ScreenState.PAUSED) {
      screen.state = ScreenState.RESUMED
      screen.resume()
    }
  }

  fun pauseScreen(screen: Screen<*, *>) {
    if (screen.state == ScreenState.RESUMED) {
      screen.state = ScreenState.PAUSED
      screen.pause()
    }
  }

  fun destroyScreen(screen: Screen<*, *>) {
    if (screen.state == ScreenState.PAUSED || screen.state == ScreenState.CREATED) {
      screen.state = ScreenState.DESTROYED
      screen.destroy()
    }
  }

  open fun <A> forward(mark: String, args: A) {
    val screen = container.instantiate(mark)
    if (screen != null) {
      if (!history.empty()) {
        val oldScreen: Screen<*, *> = history.peek().screen
        pauseScreen(oldScreen)
        container.detach(oldScreen)
      }
      if (args != null) {
        (screen as Screen<*, A>).args = args
      }
      history.push(StackEntry(mark, screen))
      createScreen(screen)
      container.attach(screen)
      resumeScreen(screen)
    }
  }

  open fun <A> replace(mark: String, args: A) {
    val screen = container.instantiate(mark)
      ?: throw Throwable("$mark screen = null")
    if (!history.empty()) {
      val oldScreen = history.peek().screen
      pauseScreen(oldScreen)
      container.detach(oldScreen)
      destroyScreen(oldScreen)
    }

    history.set(StackEntry(mark, screen))
    if (args != null) {
      (screen as Screen<*, A>).args = args
    }
    createScreen(screen)
    container.attach(screen)
    resumeScreen(screen)
  }

  open fun <A> back(mark: String, args: A): Boolean {
    val current: StackEntry<*> = history.pop()

    if (history.empty()) {
      return false
    }
    var backTo: StackEntry<*> = history.peek()

    pauseScreen(current.screen)
    container.detach(current.screen)
    destroyScreen(current.screen)

    while (history.size > 1) {
      if (backTo.mark == mark) {
        break
      }
      pauseScreen(backTo.screen)
      destroyScreen(backTo.screen)
      backTo = history.pop()
    }
    container.attach(backTo.screen)

    if (args != Unit) {
      (backTo.screen as Screen<Router, A>).args = args
    }
    resumeScreen(backTo.screen)
    return true
  }

  open fun <A> root(mark: String, args: A) {
    if (!history.empty()) {
      var screen = currentScreen()
      pauseScreen(screen)
      container.detach(screen)
      destroyScreen(screen)
      while (history.size > 0) {
        screen = history.pop().screen
        pauseScreen(screen)
        destroyScreen(screen)
      }
    }
    forward(mark, args)
  }

  open fun back(): Boolean {
    return if (history.size > 1) {
      back(history.get(history.size - 2).mark, Unit)
    } else {
      false
    }
  }

  fun currentScreen(): Screen<*, *> {
    return history.peek().screen
  }

  fun isEmpty(): Boolean {
    return history.size == 0
  }
}

class Stack<T> {

  var size: Int = 0
  private var last: Node<T>? = null

  private fun checkNotNull() {
    if (last == null) {
      throw Throwable("Stack is empty")
    }
  }

  private fun checkBounds(pos: Int) {
    if (size < pos) {
      throw Throwable("Index out of bounds: $pos >= $size")
    }
  }

  fun peek(): T {
    checkNotNull()
    return last!!.obj
  }

  fun pop(): T {
    checkNotNull()
    val obj = last!!.obj
    val prevNode = last!!.prev
    last = prevNode
    size--
    return obj
  }

  fun empty(): Boolean {
    return last == null
  }

  fun push(value: T) {
    val newNode = Node(last, value)
    last = newNode
    size++
  }

  fun set(value: T) {
    checkNotNull()
    val replaceNode = Node(last!!.prev, value)
    last = replaceNode
  }

  fun get(pos: Int): T {
    checkNotNull()
    checkBounds(pos)
    var current = last
    for (index in 0..size - pos) {
      current = last!!.prev
    }
    return current!!.obj
  }

  class Node<U>(val prev: Node<U>?, val obj: U)
}

data class StackEntry<A>(val mark: String, val screen: Screen<*, A>)

