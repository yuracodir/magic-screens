package com.yuracodir.screens

interface Command<T> {
  val mark: String
  val data: T

  fun applyCommand(history: MutableList<Command<*>>)
}

abstract class Router {
  protected val history = mutableListOf<Command<*>>()

  override fun toString() = history.toString()
  fun isEmpty() = history.size == 0
  fun size() = history.size

  protected open fun applyCommand(vararg command: Command<*>) {
    command.forEach {
      it.applyCommand(history)
    }
  }
}