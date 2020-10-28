package command.actions

import app.State
import command.Command
import fs.Directory

case class Mkdir(tokens: Vector[String]) extends Command {
  override def apply(state: State): (State, Option[String]) = {
    if (tokens.isEmpty) (state, Some("usage: mkdir directory..."))
    else {
      val dirName: String = tokens.head
      val updatedContents = Directory.addEntry(Directory(dirName), state.currentDirectory).contents
      val updatedRoot = Directory.updateContents(state.currentPath, updatedContents, state.root)

      (state.copy(root = updatedRoot), None)
    }
  }
}
