package fs

import scala.annotation.tailrec
import scala.util.Try

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

  val someEntriesExist: (List[FileEntry], Directory) => Boolean = (entries, dir) => {
    entries.map(entry => findByName(entry.name, dir.contents)).exists { _.isDefined }
  }

  val addEntry: FileEntry => Directory => Directory = entry => dir => {
    if (someEntriesExist(List(entry), dir)) throw new RuntimeException(s"${entry.name} already exists")

    val entryWithParent = entry match {
      case file: File => file.copy(parent = Some(dir))
      case directory: Directory => directory.copy(parent = Some(dir))
      case _ => entry
    }

    dir.copy(contents = dir.contents :+ entryWithParent)
  }

  val addEntrySafe: FileEntry => Directory => Try[Directory] = entry => dir => {
    Try(addEntry(entry)(dir))
  }

  @tailrec
  val addEntries: List[FileEntry] => Directory => Directory = entries => dir => {
    entries match {
      case head :: Nil => addEntry(head)(dir)
      case head :: tail => addEntry(head)(addEntries(tail)(dir))
    }
  }

  val addEntriesSafe: List[FileEntry] => Directory => Try[Directory] = entries => dir => {
    Try(addEntries(entries)(dir))
  }

  val removeEntry: (Directory, String) => Directory = (dir, entryName) => {
    if (findByName(entryName, dir.contents).isEmpty) throw new RuntimeException(s"$entryName does not exist")
    dir.copy(contents = dir.contents.filterNot(entry => entry.name == entryName))
  }

  val removeEntrySafe: (Directory, String) => Try[Directory] = (dir, entryName) => {
    Try(removeEntry(dir, entryName))
  }

  @tailrec
  val findByName: (String, List[FileEntry]) => Option[FileEntry] = (name, contents) => {
    contents match {
      case head :: _ if head.name == name => Some(head)
      case _ :: tail => findByName(name, tail)
      case _ => None
    }
  }

  val findEntryByPath: (Directory, String) => Option[FileEntry] = (root, path) => {
    val splitPath: List[String] = path.split(SEPARATOR).toList.filter(_.nonEmpty)

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
          case Some(file: File) if tail.isEmpty => Some(file)
          case _ => None
        }
        case _ => None
      }
    }

    find(splitPath, root)
  }
}