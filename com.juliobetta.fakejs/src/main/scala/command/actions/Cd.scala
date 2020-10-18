package command.actions

import app.State
import command.Command

case class Cd(override val tokens: Array[String]) extends Action(tokens) with Command {
  override def apply(state: State): (State, Option[String]) = (state, Some("cd"))
}
