package fs

case class File(
                 override val name: String,
                 override val parent: Option[Directory] = None,
                 contents: Option[String] = None
               ) extends FileEntry(name, parent)