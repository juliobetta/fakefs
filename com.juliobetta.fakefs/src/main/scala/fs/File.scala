package fs

case class File(
  override val name: String,
  contents: Option[String] = None,
  override val parent: Option[Directory] = None
) extends FileEntry(name, parent)

object File {
  val addContents: (String, File) => File = (contents, file) => {
    file.copy(contents = Some(contents))
  }

  val appendContents: (String, File) => File = (contents, file) => {
    file.copy(contents = Some(file.contents.getOrElse("") concat contents))
  }
}