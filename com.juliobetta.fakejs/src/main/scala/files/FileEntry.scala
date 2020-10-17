package files

abstract class FileEntry(val name: String) {
  override def toString: String = name
}