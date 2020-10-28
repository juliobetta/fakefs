package app

import command.{Command, Input}
import fs.Directory

object Playground extends App {
  val root = Directory.empty

  val initialState = State(Directory.ROOT_PATH, root)

  Input.newLine()

  io.Source.stdin.getLines().foldLeft(initialState)((state, line) => {
    val (newState, output) = Command.from(line)(state)

    println(output.getOrElse(""))
    Input.newLine()

    newState
  })
}
