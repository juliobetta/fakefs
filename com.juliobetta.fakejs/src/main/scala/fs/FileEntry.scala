package fs

abstract class FileEntry(val name: String, val parent: Option[Directory]) {
  override def toString: String = name
}