package com.yuracodir.screens

open class Command<T>(val mark: String, val data: T) {
  override fun toString() = mark
}

abstract class Router {
  protected val history: Stack<Command<*>> = Stack()

  open fun chain(vararg commands: Command<*>) {
    if (commands.isNotEmpty()) {
      history.clear()
      commands.forEach {
        history.push(it)
      }
      navigateTo(commands.last())
    }
  }

  open fun forward(command: Command<*>?) {
    command?.let {
      history.push(command)
      navigateTo(command)
    }
  }

  open fun replace(command: Command<*>?) {
    command?.let {
      history.pop()
      history.push(command)
      navigateTo(command)
    }
  }

  open fun root(command: Command<*>?) {
    command?.let {
      history.clear()
      history.push(command)
      navigateTo(command)
    }
  }

  open fun back(command: Command<*>?): Boolean {
    if (history.size > 0) {
      command?.let {
        history.pop()
        navigateTo(command)
        return true
      }
    }
    return false
  }

  override fun toString() = history.toString()
  fun isEmpty() = history.size == 0
  fun size() = history.size

  protected abstract fun navigateTo(command: Command<*>)
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

  operator fun get(pos: Int): T {
    checkNotNull()
    checkBounds(pos)
    var current = last
    for (index in 0..size - pos) {
      current = last!!.prev
    }
    return current!!.obj
  }

  operator fun iterator(): Iterator<T> {
    return object : Iterator<T> {
      private var index = 0
      private var item = last

      override fun hasNext(): Boolean = index < size && item?.prev != null

      override fun next(): T {
        val it = item
        if (!hasNext() || it == null) {
          throw NoSuchElementException()
        }
        index++
        item = it.prev
        return item!!.obj
      }
    }
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
    last = null
    size = 0
  }

  class Node<U>(val prev: Node<U>?, val obj: U)
}
