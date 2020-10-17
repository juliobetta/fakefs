package files

case class File(override val name: String, contents: Option[String] = None) extends FileEntry(name)
