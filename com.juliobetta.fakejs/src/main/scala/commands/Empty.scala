package commands

import app.State

object Empty extends Command {
  override def apply(state: State): (State, Option[String]) = (state, None)
}
