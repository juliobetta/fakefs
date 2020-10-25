package command

import command.actions.Cd

case class Input(input: String) {
  val tokens: Vector[String] = input.split(" ").toVector
  val isEmpty: Boolean = input.isEmpty || tokens.isEmpty
  val command: Command = tokens.head match {
    case "cd" => Cd(tokens.tail)
    case _ => Invalid
  }
}
