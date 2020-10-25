package command.actions

import app.State
import command.Command

case class Mkdir(tokens: Vector[String]) extends Command {
  val path: String = tokens.head

  override def apply(state: State): (State, Option[String]) = (state, None)
}
