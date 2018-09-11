package com.estudio.magic

interface ContainerScreen {
  val childRouter: Router
  fun attach(screen: Screen<*, *>)
  fun detach(screen: Screen<*, *>)
  fun instantiate(mark: String): Screen<*, *>?
}

interface Screen<R : Router, A : Any> {
  var args: A?
  var state: ScreenState
  var router: R
  fun create() // do not touch this.router here
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

open class Router(protected val container: ContainerScreen) {
  private val history: Stack<StackEntry> = Stack()

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

  open fun forward(mark: String, args: Any? = null) {
    val screen = container.instantiate(mark)
    if (screen != null) {
      if (!history.empty()) {
        val oldScreen: Screen<*, *> = history.peek().screen
        pauseScreen(oldScreen)
        container.detach(oldScreen)
      }
      (screen as Screen<*, Any>).args = args
      history.push(StackEntry(mark, screen))
      createScreen(screen)
      container.attach(screen)
      resumeScreen(screen)
    }
  }

  open fun replace(mark: String, args: Any? = null) {
    val screen = container.instantiate(mark)
      ?: throw Throwable("$mark screen = null")
    if (!history.empty()) {
      val oldScreen = history.peek().screen
      pauseScreen(oldScreen)
      container.detach(oldScreen)
      destroyScreen(oldScreen)
    }

    history.set(StackEntry(mark, screen))
    (screen as Screen<*, Any>).args = args
    createScreen(screen)
    container.attach(screen)
    resumeScreen(screen)
  }

  open fun back(mark: String, args: Any? = null): Boolean {
    if (history.empty()) {
      return false
    }
    var backTo: StackEntry = history.peek()
    pauseScreen(backTo.screen)
    container.detach(backTo.screen)

    while (history.size > 0) {
      backTo = history.pop()
      if (backTo.mark == mark) {
        break
      }
      destroyScreen(backTo.screen)
    }

    history.push(backTo)
    (backTo.screen as Screen<*, Any>).args = args
    container.attach(backTo.screen)
    resumeScreen(backTo.screen)
    return true
  }

  open fun root(mark: String, args: Any) {
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

  open fun back(args: Any? = null): Boolean {
    return if (history.size > 1) {
      back(history.get(history.size - 2).mark, args)
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

data class StackEntry(val mark: String, val screen: Screen<*, *>)

