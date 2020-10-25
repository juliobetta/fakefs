package fixtures

import fs.{Directory, File}

object FileTree {
  val file1: File = File("my-file01", Some("I have something 01"))
  val file2: File = File("my-file02", Some("I have something 02"))
  val file3: File = File("my-file03", Some("I have something 03"))
  val file4: File = File("my-file04", Some("I have something 04"))
  val file5: File = File("my-file05", Some("I have something 05"))

  val dir0: Directory = Directory("dir00")
  val dir1: Directory = Directory.addEntries(Vector(file1, file2))(Directory("dir01"))
  val dir2: Directory = Directory.addEntries(Vector(dir0, file3))(Directory("dir02"))
  val dir3: Directory = Directory.addEntries(Vector(dir1, dir2))(Directory("dir03"))

  val root: Directory = Directory.addEntries(Vector(dir3, file4, file5))(Directory.empty)
}
