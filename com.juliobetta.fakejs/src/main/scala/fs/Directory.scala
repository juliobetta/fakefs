package fs

import scala.annotation.tailrec

case class Directory(
                      override val name: String,
                      override val parent: Option[Directory] = None,
                      contents: List[FileEntry] = Nil
                    ) extends FileEntry(name, parent) {
//  def printFilesTree(): Unit = {
//    @tailrec
//    def output(entries: List[FileEntry], acc: List[String]): List[String] =
//      entries match {
//        case Nil => acc
//        case (dir: Directory) :: tail => output(dir.contents ++ tail, acc :+ s"${dir}")
//        case head :: tail => output(tail, acc :+ s"${head}")
//      }
//
//    output(contents, Nil).foreach { println }
//  }
}

object Directory {
  val ROOT_PATH: String = "/"
  val SEPARATOR: String = "/"

  val empty: Directory = Directory("", None)

  val addEntry: FileEntry => Directory => Directory = entry => dir => {
    val entryWithParent = entry match {
      case file: File => file.copy(parent = Some(dir))
      case directory: Directory => directory.copy(parent = Some(dir))
      case _ => entry
    }

    dir.copy(contents = dir.contents :+ entryWithParent)
  }

  @tailrec
  val addEntries: List[FileEntry] => Directory => Directory = entries => dir => {
    entries match {
      case head :: Nil => addEntry(head)(dir)
      case head :: tail => addEntries(tail)(addEntry(head)(dir))
    }
  }

  val removeEntry: (Directory, String) => Directory = (dir, entryName) => {
    dir.copy(contents = dir.contents.filterNot(entry => entry.name == entryName))
  }

  val findEntryByPath: (Directory, String) => Option[FileEntry] = (root, path) => {
    val splitPath: List[String] = path.split(SEPARATOR).toList

    @tailrec
    def findByName(name: String, contents: List[FileEntry]): Option[FileEntry] = {
      contents match {
        case (_: File) :: tail if tail.nonEmpty => None // if file is in the middle of path, return NONE
        case head :: _ if head.name == name => Some(head)
        case _ :: tail => findByName(name, tail)
        case _ => None
      }
    }

    @tailrec
    def find(entries: List[String], acc: Directory): Option[FileEntry] = {
      entries match {
        case Nil => Some(acc)
        case head :: tail if head == "." => find(tail, acc)
        case head :: tail if head == ".." => acc.parent match {
          case Some(parentDir) => find(tail, parentDir)
          case _ => None
        }
        case head :: tail => findByName(head, acc.contents) match {
          case Some(dir: Directory) if tail.isEmpty => Some(dir)
          case Some(dir: Directory) if tail.nonEmpty => find(tail, dir)
          case Some(file: File) => Some(file)
        }
        case _ => None
      }
    }

    find(splitPath, root)
  }
}