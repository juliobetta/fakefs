package command.actions

import app.State
import command.{Command, Input}

case class Cd(tokens: Array[String]) extends Command {
  override def apply(state: State): (State, Option[String]) = (state, Some("cd"))
}
