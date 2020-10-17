package commands

import commands.actions.Cd

case class Input(input: String) {
  val tokens: Array[String] = input.split(" ")
  val isEmpty: Boolean = input.isEmpty || tokens.isEmpty
  val command: Command = tokens(0) match {
    case "cd" => Cd(tokens)
    case _ => Invalid
  }
}
