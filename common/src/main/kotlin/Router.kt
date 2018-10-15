package com.estudio.magic
//
//interface ContainerScreen {
//  val childRouter: Router
//  fun navigateTo(mark: String?, screen: Screen<*>)
//  fun detach(screen: Screen<*>)
//  fun instantiate(mark: String): Screen<*>?
//}
//
//interface Screen<R : Router, A : Any> {
//  var args: A?
//  //  var state: ScreenState
//  var router: R
//
//  fun create() // do not touch this.router here
//  fun pause()
//  fun resume()
//  fun destroy()
//  fun onBack(): Boolean // is Handled ?
//}
//
//interface ScreenLifecycle {
//  fun create(screen: Screen<*>)
//  fun pause(screen: Screen<*>)
//  fun resume(screen: Screen<*>)
//  fun destroy(screen: Screen<*>)
//}
//
//enum class ScreenState {
//  NONE,
//  CREATED,
//  RESUMED,
//  PAUSED,
//  DESTROYED,
//}
//
////open class DefaultScreenLifecycle :ScreenLifecycle {
////  override fun create(screen: Screen<*>) {
////    if (screen.state == ScreenState.NONE) {
////      screen.state = ScreenState.CREATED
////      screen.create()
////    }
////  }
////
////  override fun resume(screen: Screen<*>) {
////    if (screen.state == ScreenState.CREATED || screen.state == ScreenState.PAUSED) {
////      screen.state = ScreenState.RESUMED
////      screen.resume()
////    }
////  }
////
////  override fun pause(screen: Screen<*>) {
////    if (screen.state == ScreenState.RESUMED) {
////      screen.state = ScreenState.PAUSED
////      screen.pause()
////    }
////  }
////
////  override fun destroy(screen: Screen<*>) {
////    if (screen.state == ScreenState.PAUSED || screen.state == ScreenState.CREATED) {
////      screen.state = ScreenState.DESTROYED
////      screen.destroy()
////    }
////  }
////}
//open class Router2(protected val container: ContainerScreen) {
//
//  private val history: Stack<StackEntry> = Stack()
//
//  open fun forward(mark: String) {
//    val screen = container.instantiate(mark)
//    screen?.let {
//      container.navigateTo(mark, screen)
//      history.push(StackEntry(mark, screen))
//    }
//  }
//
//  open fun replace(mark: String) {
//    val screen = container.instantiate(mark)
//    screen?.let {
//      container.navigateTo(mark, screen)
//      history.pop()
//      history.push(StackEntry(mark, screen))
//    }
//  }
//
//  open fun root(mark: String) {
//    val screen = container.instantiate(mark)
//    screen?.let {
//      container.navigateTo(mark, screen)
//      history.clear()
//      history.push(StackEntry(mark, screen))
//    }
//  }
//
//  open fun back(mark: String) {
//    do {
//      val backTo = history.pop()
//      container.navigateTo(mark, backTo.screen)
//    } while (mark != backTo.mark)
//  }
//}
//
//open class Router(
//  protected val container: ContainerScreen
//  /*val lifecycle: ScreenLifecycle = DefaultScreenLifecycle()*/
//                 ) {
//
//  private val history: Stack<StackEntry> = Stack()
//
//  open fun forward(mark: String, args: Any? = null) {
//    val screen = container.instantiate(mark)
//    if (screen != null) {
//      if (!history.empty()) {
//        val oldScreen: Screen<*> = history.peek().screen
//        //        lifecycle.pause(oldScreen)
//        //        container.detach(oldScreen)
//      }
//      (screen as Screen<*, Any>).args = args
//      history.push(StackEntry(mark, screen))
//      //      lifecycle.create(screen)
//      container.navigateTo(mark, screen)
//      //      lifecycle.resume(screen)
//    }
//  }
//
//  open fun replace(mark: String, args: Any? = null) {
//    val screen = container.instantiate(mark)
//      ?: throw Throwable("$mark screen = null")
//    if (!history.empty()) {
//      val oldScreen = history.peek().screen
//      //      lifecycle.pause(oldScreen)
//      //      container.detach(oldScreen)
//      //      lifecycle.destroy(oldScreen)
//    }
//
//    history.set(StackEntry(mark, screen))
//    (screen as Screen<*, Any>).args = args
//    //    lifecycle.create(screen)
//    container.navigateTo(mark, screen)
//    //    lifecycle.resume(screen)
//  }
//
//  open fun back(mark: String? = null, args: Any? = null): Boolean {
//    if (history.empty()) {
//      return false
//    }
//    var marker = mark
//    if (marker == null) {
//      if (history.size > 1) {
//        marker = history.get(history.size - 2).mark
//      } else {
//        return false
//      }
//    }
//    var screen: StackEntry = history.pop()
//    //    lifecycle.pause(screen.screen)
//    //    container.detach(screen.screen)
//    //    lifecycle.destroy(screen.screen)
//    screen = history.peek()
//    while (history.size > 0) {
//      screen = history.pop()
//      if (screen.mark == marker) {
//        break
//      }
//      //      lifecycle.destroy(screen.screen)
//    }
//
//    history.push(screen)
//    (screen.screen as Screen<*, Any>).args = args
//    container.navigateTo(mark, screen.screen)
//    //    lifecycle.resume(screen.screen)
//    return true
//  }
//
//  open fun root(mark: String, args: Any? = null) {
//    if (!history.empty()) {
//      var screen = currentScreen()
//      //      lifecycle.pause(screen)
//      //      container.detach(screen)
//      //      lifecycle.destroy(screen)
//      while (history.size > 0) {
//        screen = history.pop().screen
//        //        lifecycle.pause(screen)
//        //        lifecycle.destroy(screen)
//      }
//    }
//    forward(mark, args)
//  }
//
//  fun currentScreen(): Screen<*> {
//    return history.peek().screen
//  }
//
//  fun isEmpty(): Boolean {
//    return history.size == 0
//  }
//
//  override fun toString() = history.toString()
//}

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

  override fun toString(): String {
    var path = ""
    var item = last
    while (item != null) {
      path = "> ${item.obj.toString()}" + path
      item = item.prev
    }
    return path
  }

  fun clear() {
    checkNotNull()
    last = null
    size = 0
  }

  class Node<U>(val prev: Node<U>?, val obj: U)
}

//data class StackEntry(val mark: String, val screen: Screen<*>) {
//  override fun toString() = mark
//}

