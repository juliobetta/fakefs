package command.actions

import app.State
import command.Command
import fs.Directory

case class Cd(tokens: Vector[String]) extends Command {
  val path: String = tokens.head

  override def apply(state: State): (State, Option[String]) = {
    Directory.findEntryByPath(path, state.currentDirectory) match {
      case Some(dir:Directory) => (state.copy(currentPath = Directory.getAbsolutePath(dir)), None)
      case _ => (state, Some(s"cd: no such directory: $path"))
    }
  }
}

