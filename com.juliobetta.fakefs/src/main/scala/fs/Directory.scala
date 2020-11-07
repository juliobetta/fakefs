package fs

import scala.annotation.tailrec
import scala.util.Try

case class Directory(
  override val name: String,
  contents: Vector[FileEntry] = Vector(),
  override val parent: Option[Directory] = None
) extends FileEntry(name, parent)

object Directory {
  val ROOT_PATH: String = "/"
  val SEPARATOR: String = "/"
  val CURRENT_DIR: String = "."
  val PARENT_DIR: String = ".."

  val empty: Directory = Directory("")

  val splitPath: String => Vector[String] = path => path.split(SEPARATOR).toVector.filter(token =>
    token.nonEmpty && token != CURRENT_DIR
  )

  val someEntriesExist: (Vector[FileEntry], Directory) => Boolean = (entries, dir) => {
    entries.map(entry => findByName(entry.name, dir.contents)).exists { _.isDefined }
  }

  val addEntry: (FileEntry, Directory) => Directory = (entry, dir) => {
    if (someEntriesExist(Vector(entry), dir)) throw new RuntimeException(s"${entry.name} already exists")

    val updatedContents = dir.contents :+ entry

    dir.copy(contents = updatedContents.map { entry =>
      val updatedDir = Some(dir.copy(contents = updatedContents))

      entry match {
        case fileEntry: File => fileEntry.copy(parent = updatedDir)
        case dirEntry: Directory => dirEntry.copy(parent = updatedDir)
      }
    })
  }

  val addEntrySafe: (FileEntry, Directory) => Try[Directory] = (entry, dir) => Try(addEntry(entry, dir))

  @tailrec
  val addEntries: (Vector[FileEntry], Directory) => Directory = (entries, dir) => {
    entries match {
      case head +: Vector() => addEntry(head, dir)
      case head +: tail => addEntry(head, addEntries(tail, dir))
      case _ => dir
    }
  }

  val addEntriesSafe: (Vector[FileEntry], Directory) => Try[Directory] = (entries, dir) => Try(addEntries(entries, dir))

  val removeEntry: (String, Directory) => Directory = (entryName, dir) => {
    if (findByName(entryName, dir.contents).isEmpty) throw new RuntimeException(s"$entryName does not exist")
    dir.copy(contents = dir.contents.filterNot(entry => entry.name == entryName))
  }

  val removeEntrySafe: (String, Directory) => Try[Directory] = (entryName, dir) => Try(removeEntry(entryName, dir))

  @tailrec
  val findByName: (String, Vector[FileEntry]) => Option[FileEntry] = (name, contents) => {
    contents match {
      case Vector() => None
      case head +: _ if head.name == name => Some(head)
      case _ +: tail => findByName(name, tail)
    }
  }

  val findEntryByPath: (String, Directory) => Option[FileEntry] = (path, rootDir) => {
    /**
     * Helper is necessary to give `findEntry` a context to call itself recursively. otherwise, it throws the following:
     * "scala forward reference extends over definition of value"
     * the alternative is declaring `findEntry` as a `def`. but I kinda like using `val` =)
     */
    object Helper {
      @tailrec
      val findEntry: (Vector[String], Directory) => Option[FileEntry] = (inputTokens, dir) => {
        inputTokens match {
          case Vector() => Some(dir)
          case head +: tail if head == PARENT_DIR => dir.parent match {
            case Some(parentDir) => findEntry(tail, parentDir)
            case _ => None
          }
          case head +: tail => findByName(head, dir.contents) match {
            case entry@Some(_) if tail.isEmpty => entry
            case Some(dir: Directory) if tail.nonEmpty => findEntry(tail, dir)
            case _ => None
          }
        }
      }
    }

    Helper.findEntry(splitPath(path), rootDir)
  }

  val resetContents: Directory => Directory = dir => dir.copy(contents = Vector())

  /**
   * Deeply update directory contents by finding the directory and then update its parents contents up to the top
   */
  val updateContents: (String, Vector[FileEntry], Directory) => Directory = (path, updatedContents, source) => {
    object Helper {
      val updateSource: Vector[FileEntry] => Directory = sourceContents => {
        (Directory.resetContents andThen Directory.addEntries.curried(sourceContents))(source)
      }

      @tailrec
      val traverse: (Vector[String], Directory, Vector[FileEntry], Int) => Directory = (tokens, dir, contents, counterAcc) => {
        tokens match {
          case Vector() => updateSource(contents)
          case head +: tail if head == PARENT_DIR => dir.parent match {
            case Some(parentDir) => traverse(tail, parentDir, contents, counterAcc)
            case _ => updateSource(contents)
          }
          case head +: tail => findByName(head, dir.contents) match {
            case Some(currentDir:Directory) if tail.isEmpty =>
              // replace old contents by removing and add the new entry
              val updatedDir =
                (Directory.removeEntry.curried(currentDir.name)
                  andThen Directory.addEntry.curried(currentDir.copy(contents = contents)))(dir)

              // update parents contents by reinitializing the process removing
              traverse(splitPath(path).dropRight(counterAcc), source, updatedDir.contents, counterAcc + 1)
            case Some(currentDir:Directory) if tail.nonEmpty => traverse(tail, currentDir, contents, counterAcc)
            case _ => updateSource(contents)
          }
        }
      }
    }

    val initialCounter = 1
    Helper.traverse(splitPath(path), source, updatedContents, initialCounter)
  }

  /**
   * Get directory absolute path from
   */
  val getAbsolutePath: Directory => String = dir => {
    object Helper {
      @tailrec
      val traverse: (Directory, Vector[String]) => Vector[String] = (dir, acc) => {
        dir.parent match {
          case Some(parent) => traverse(parent, acc :+ parent.name)
          case _ => acc
        }
      }
    }

    Helper.traverse(dir, Vector(dir.name)).reverse.mkString(SEPARATOR)
  }
}