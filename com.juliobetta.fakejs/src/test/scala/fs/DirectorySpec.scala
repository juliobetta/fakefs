package fs

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class DirectorySpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  val file1: File = File("my-file01", None, Some("I have something 01"))
  val file2: File = File("my-file02", None, Some("I have something 02"))
  val file3: File = File("my-file03", None, Some("I have something 03"))

  describe("Directory") {
    describe("addEntry()") {
      val dir = (Directory.addEntry(file1) andThen Directory.addEntry(file2))(Directory("dir01"))

      it("sets parent directory to entries") {
        dir.contents.map(_.parent.get.name).distinct must equal(List(dir.name))
      }

      it("adds an entry at a time into a directory") {
        dir.contents.map(_.name) must contain allOf (file1.name, file2.name)
      }

      describe("when entry already exist in the directory") {
        it("fails") {
          Directory.addEntrySafe(file1)(dir).isFailure must equal(true)
        }
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

      describe("when some entry already exist in the directory") {
        it("fails") {
          Directory.addEntriesSafe(List(file1, file3))(dir).isFailure must equal(true)
        }
      }
    }

    describe("removeEntry()") {
      val entries = List(file1, file2)
      val dir = Directory.addEntries(entries)(Directory("dir01"))

      it("removes an entry from a directory") {
        val updatedDir = Directory.removeEntry(dir, file1.name)
        val entryNames = updatedDir.contents.map(_.name)

        entryNames must not contain file1.name
        entryNames must contain (file2.name)
      }

      describe("when entry is not found") {
        it("it fails") {
          Directory.removeEntrySafe(dir, "unknown").isFailure must equal(true)
        }
      }
    }

    describe("findByName()") {
      val entries = List(file1, file2)
      val dir = Directory.addEntries(entries)(Directory("dir01"))

      it("finds entry by name") {
        val found = Directory.findByName("my-file01", dir.contents)

        found.map(_.name).head must equal(file1.name)
      }

      describe("when entry is not found") {
        it("returns None") {
          val unknown = Directory.findByName("unknown", dir.contents)

          unknown must equal(None)
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
        val foundFile = Directory.findEntryByPath(root, "dir03/dir02/my-file03")
        val foundDir = Directory.findEntryByPath(root, "dir03/dir02")

        foundFile.map(_.name).head must equal(file3.name)
        foundDir.map(_.name).head must equal(dir2.name)
      }

      describe("when path is empty") {
        it("returns the current dir") {
          val entry = Directory.findEntryByPath(root, "")

          entry must equal(Some(root))
        }
      }

      describe("when file is in the middle of path") {
        it("returns None") {
          val entry = Directory.findEntryByPath(root, "my-file04/dir03")

          entry must equal(None)
        }
      }
    }
  }
}
