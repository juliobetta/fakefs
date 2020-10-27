package command.actions

import app.State
import command.Command
import fs.Directory

case class Cd(tokens: Vector[String]) extends Command {
  val path: String = tokens.head

  override def apply(state: State): (State, Option[String]) = {
    Directory.findEntryByPath(path, state.root) match {
      case Some(_:Directory) => (state.copy(currentPath = path), None)
      case _ => (state, Some(s"cd: no such directory: $path"))
    }
  }
}

object Cd {
  override def toString: String = "cd"
}
