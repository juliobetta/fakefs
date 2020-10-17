package commands

import app.State

trait Command extends (State => (State, Option[String]))

object Command {
  val from: String => Command = (input: String) => Input(input) match {
    case input if input.isEmpty => Empty
    case input => input.command
  }
}
