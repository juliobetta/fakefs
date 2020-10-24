package fs

import scala.annotation.tailrec
import scala.util.Try

case class Directory(
  override val name: String,
  override val parent: Option[Directory] = None,
  contents: List[FileEntry] = Nil
) extends FileEntry(name, parent)

object Directory {
  val ROOT_PATH: String = "/"
  val SEPARATOR: String = "/"

  val empty: Directory = Directory("", None)

  val someEntriesExist: (List[FileEntry], Directory) => Boolean = (entries, dir) => {
    entries.map(entry => findByName(entry.name, dir.contents)).exists { _.isDefined }
  }

  val addEntry: FileEntry => Directory => Directory = entry => dir => {
    if (someEntriesExist(List(entry), dir)) throw new RuntimeException(s"${entry.name} already exists")

    val updatedContents = dir.contents :+ entry

    dir.copy(contents = updatedContents.map { entry =>
      val updatedDir = Some(dir.copy(contents = updatedContents))

      entry match {
        case fileEntry: File => fileEntry.copy(parent = updatedDir)
        case dirEntry: Directory => dirEntry.copy(parent = updatedDir)
      }
    })
  }

  val addEntrySafe: FileEntry => Directory => Try[Directory] = entry => dir => Try(addEntry(entry)(dir))

  @tailrec
  val addEntries: List[FileEntry] => Directory => Directory = entries => dir => {
    entries match {
      case head :: Nil => addEntry(head)(dir)
      case head :: tail => addEntry(head)(addEntries(tail)(dir))
      case _ => dir
    }
  }

  val addEntriesSafe: List[FileEntry] => Directory => Try[Directory] = entries => dir => Try(addEntries(entries)(dir))

  val removeEntry: (Directory, String) => Directory = (dir, entryName) => {
    if (findByName(entryName, dir.contents).isEmpty) throw new RuntimeException(s"$entryName does not exist")
    dir.copy(contents = dir.contents.filterNot(entry => entry.name == entryName))
  }

  val removeEntrySafe: (Directory, String) => Try[Directory] = (dir, entryName) => Try(removeEntry(dir, entryName))

  @tailrec
  val findByName: (String, List[FileEntry]) => Option[FileEntry] = (name, contents) => {
    contents match {
      case Nil => None
      case head :: _ if head.name == name => Some(head)
      case _ :: tail => findByName(name, tail)
      case _ => None
    }
  }

  val findEntryByPath: (Directory, String) => Option[FileEntry] = (root, path) => {
    val splitPath: List[String] = path.split(SEPARATOR).toList.filter(_.nonEmpty)

    /**
     * Helper is necessary to give `findEntry` a context to call itself recursively. otherwise, it throws the following:
     * "scala forward reference extends over definition of value"
     * the alternative is declaring `findEntry` as a `def`. but I kinda like using `val` =)
     */
    object Helper {
      @tailrec
      val findEntry: (List[String], Directory) => Option[FileEntry] = (inputTokens, dir) => {
        inputTokens match {
          case Nil => Some(dir)
          case head :: tail if head == "." => findEntry(tail, dir)
          case head :: tail if head == ".." => dir.parent match {
            case Some(parentDir) => findEntry(tail, parentDir)
            case _ => None
          }
          case head :: tail => findByName(head, dir.contents) match {
            case entry@Some(_) if tail.isEmpty => entry
            case Some(dir: Directory) if tail.nonEmpty => findEntry(tail, dir)
            case _ => None
          }
        }
      }
    }

    Helper.findEntry(splitPath, root)
  }
}