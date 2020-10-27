package app

import fs.Directory

case class State(currentPath: String,  root: Directory) {
  val currentDirectory: Directory = Directory.findEntryByPath(currentPath, root) match {
    case Some(dir: Directory) => dir
    case _ => root
  }
}