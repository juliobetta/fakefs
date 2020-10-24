package command.actions

import app.State
import command.Command

case class Cd(val tokens: List[String]) extends Command {
  val path: String = tokens.head

  override def apply(state: State): (State, Option[String]) = (state, Some("cd"))
}
