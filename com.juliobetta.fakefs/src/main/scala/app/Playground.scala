package app

import command.Command
import fs.{Directory, File}

object Playground extends App {
  val root = Directory("root")

  val state = State(Directory.ROOT_PATH, root)
  val (newState, output) = Command.from("cd some/path")(state)

  output match {
    case Some(output) => println(output)
    case None => println("empty")
  }
}
