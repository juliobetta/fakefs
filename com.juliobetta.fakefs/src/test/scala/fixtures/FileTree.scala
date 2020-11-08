package fixtures

import fs.{Directory, File}

object FileTree {
  val rootSource: Directory = Directory.empty
  val dir3Source: Directory = Directory("dir03", parent = Some(rootSource))
  val dir2Source: Directory = Directory("dir02", parent = Some(dir3Source))
  val dir1Source: Directory = Directory("dir01", parent = Some(dir3Source))

  val file1: File = File("my-file01", Some("I have something 01"), parent = Some(dir1Source))
  val file2: File = File("my-file02", Some("I have something 02"), parent = Some(dir1Source))
  val file3: File = File("my-file03", Some("I have something 03"), parent = Some(dir2Source))
  val file4: File = File("my-file04", Some("I have something 04"), parent = Some(rootSource))
  val file5: File = File("my-file05", Some("I have something 05"), parent = Some(rootSource))

  val dir0: Directory = Directory("dir00", parent = Some(dir2Source))
  val dir1: Directory = Directory.addEntries(Vector(file1, file2), dir1Source)
  val dir2: Directory = Directory.addEntries(Vector(dir0, file3), dir2Source)
  val dir3: Directory = Directory.addEntries(Vector(dir1, dir2), dir3Source)

  val root: Directory = Directory.addEntries(Vector(dir3, file4, file5), rootSource)
}
