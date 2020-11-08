package command.actions

import app.State
import command.Command
import fs.{Directory, FileEntry}

case class Ls(tokens: Vector[String]) extends Command {
  val path: String = {
    if (tokens.isEmpty) ""
    else tokens.head
  }

  override def apply(state: State): (State, Option[String]) = {
    val printContents: Vector[FileEntry] => String = contents => contents.sortBy(_.name).mkString("\n")

    if (path.isEmpty) (state, Some(printContents(state.currentDirectory.contents)))
    else Directory.findEntryByPath(path, state.currentDirectory) match {
      case Some(dir: Directory) => (state, Some(printContents(dir.contents)))
      case _ => (state, Some(s"cd: no such directory: $path"))
    }
  }
}
