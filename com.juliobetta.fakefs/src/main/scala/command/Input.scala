package command

import command.actions.{Cd, Mkdir}

case class Input(input: String) {
  val tokens: Vector[String] = input.split(" ").toVector
  val isEmpty: Boolean = input.isEmpty || tokens.isEmpty
  val command: Command = tokens.head match {
    case Cd.toString => Cd(tokens.tail)
    case Mkdir.toString => Mkdir(tokens.tail)
    case _ => Invalid
  }
}
