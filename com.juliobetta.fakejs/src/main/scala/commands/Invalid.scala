package commands
import app.State

object Invalid extends Command {
  override def apply(state: State): (State, Option[String]) = (state, Some("Error: Invalid Command"))
}
