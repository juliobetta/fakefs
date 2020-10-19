package fs

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class DirectorySpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  val file1: File = File("my-file01", None, Some("I have something 01"))
  val file2: File = File("my-file02", None, Some("I have something 02"))

  describe("Directory") {
    describe("addEntry()") {
      val dir = (Directory.addEntry(file1) andThen Directory.addEntry(file2))(Directory("dir01"))

      it("sets parent directory to entries") {
        dir.contents.map(_.parent.get.name).distinct must equal(List(dir.name))
      }

      it("adds an entry at a time into a directory") {
        dir.contents.map(_.name) must contain allOf (file1.name, file2.name)
      }
    }

    describe("addEntries()") {
      val entries = List(file1, file2)
      val dir = Directory.addEntries(entries)(Directory("dir01"))

      it("sets parent directory to entries") {
        dir.contents.map(_.parent.get.name).distinct must equal(List(dir.name))
      }

      it("adds multiple entries into a directory") {
        dir.contents.map(_.name) must contain allOf (file1.name, file2.name)
      }
    }

    describe("removeEntry()") {
      val entries = List(file1, file2)
      val dir = Directory.addEntries(entries)(Directory("dir01"))

      it("removes an entry from a directory") {
        val updatedDir = Directory.removeEntry(dir, file1.name)
        val entryNames = updatedDir.contents.map(_.name)

        entryNames must not contain (file1.name)
        entryNames must contain (file2.name)
      }

      describe("when entry is not found") {
        it("returns the same directory contents") {
          val updatedDir = Directory.removeEntry(dir, "unknown")
          val entryNames = updatedDir.contents.map(_.name)

          entryNames must contain (file1.name, file2.name)
        }
      }
    }

    describe("findEntryByPath()") {
      val file1 = File("my-file01", None, Some("I have something 01"))
      val file2 = File("my-file02", None, Some("I have something 02"))
      val file3 = File("my-file03", None, Some("I have something 03"))
      val file4 = File("my-file04", None, Some("I have something 04"))
      val file5 = File("my-file05", None, Some("I have something 05"))

      val dir1 = Directory.addEntries(List(file1, file2))(Directory("dir01"))
      val dir2 = Directory.addEntry(file3)(Directory("dir02"))
      val dir3 = Directory.addEntries(List(dir1, dir2))(Directory("dir03"))

      val root = Directory.addEntries(List(dir3, file4, file5))(Directory.empty)

      it("finds an entry by its path") {
        val found = Directory.findEntryByPath(root, "dir03/dir02/my-file03")
        found.get must equal(file3)

        // cover other cases. e.g. .././dir01
      }
    }
  }
}
