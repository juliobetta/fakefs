package app

import fs.{Directory, FileEntry}

case class State(currentPath: String,  root: Directory) {
  val currentDirectory: Directory = Directory.findEntryByPath(root, currentPath) match {
    case Some(dir: Directory) => dir
    case _ => root
  }
}