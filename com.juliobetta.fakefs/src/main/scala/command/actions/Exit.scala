package command.actions

import app.State
import command.Command

case class Exit() extends Command {
  override def apply(state: State): (State, Option[String]) = {
    System.exit(0)
    (state, None)
  }
}
