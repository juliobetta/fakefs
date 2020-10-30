package fs

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class DirectorySpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  import fixtures.FileTree._

  describe("Directory") {
    describe("addEntry()") {
      val dir = (Directory.addEntry.curried(file1) andThen Directory.addEntry.curried(file2))(Directory("dir01"))

      it("sets parent directory to entries") {
        dir.contents.map(_.parent.get.name).distinct mustEqual Vector(dir.name)
      }

      it("adds an entry at a time into a directory") {
        val expected = Vector(file1.name, file2.name)
        dir.contents.map(_.name) must contain theSameElementsAs expected
      }

      describe("when entry already exist in the directory") {
        it("fails") {
          Directory.addEntrySafe(file1, dir).isFailure mustBe true
        }
      }
    }

    describe("addEntries()") {
      val entries = Vector(file1, file2)
      val dir = Directory.addEntries(entries, Directory("dir01"))

      it("sets parent directory to entries") {
        dir.contents.map(_.parent.get.name).distinct mustEqual Vector(dir.name)
      }

      it("adds multiple entries into a directory") {
        val expected = Vector(file1.name, file2.name)
        dir.contents.map(_.name) must contain theSameElementsAs expected
      }

      describe("when some entry already exist in the directory") {
        it("fails") {
          Directory.addEntriesSafe(Vector(file1, file3), dir).isFailure mustBe true
        }
      }
    }

    describe("removeEntry()") {
      val entries = Vector(file1, file2)
      val dir = Directory.addEntries(entries, Directory("dir01"))

      it("removes an entry from a directory") {
        val updatedDir = Directory.removeEntry(file1.name, dir)
        val entryNames = updatedDir.contents.map(_.name)

        entryNames must not contain file1.name
        entryNames must contain (file2.name)
      }

      describe("when entry is not found") {
        it("it fails") {
          Directory.removeEntrySafe("unknown", dir).isFailure mustBe true
        }
      }
    }

    describe("findByName()") {
      val entries = Vector(file1, file2)
      val dir = Directory.addEntries(entries, Directory("dir01"))

      it("finds entry by name") {
        val found = Directory.findByName("my-file01", dir.contents)

        found.map(_.name).head mustEqual file1.name
      }

      describe("when entry is not found") {
        it("returns None") {
          val unknown = Directory.findByName("unknown", dir.contents)

          unknown mustBe None
        }
      }
    }

    describe("findEntryByPath()") {
      val dir0 = Directory("dir00")
      val dir1 = Directory.addEntries(Vector(file1, file2), Directory("dir01"))
      val dir2 = Directory.addEntries(Vector(dir0, file3), Directory("dir02"))
      val dir3 = Directory.addEntries(Vector(dir1, dir2), Directory("dir03"))

      val root = Directory.addEntries(Vector(dir3, file4, file5), Directory.empty)

      it("finds an entry by its path") {
        val foundFile = Directory.findEntryByPath("dir03/dir02/my-file03", root)
        val foundDir = Directory.findEntryByPath("dir03/dir02", root)

        foundFile.map(_.name).head mustEqual file3.name
        foundDir.map(_.name).head mustEqual dir2.name
      }

      describe("when single dot is present in the path") {
        it("keeps the current directory") {
          val foundFile = Directory.findEntryByPath("dir03/./././dir02", root)

          foundFile.map(_.name).head mustEqual dir2.name
        }
      }

      describe("when double dots is present in the path") {
        it("switches to the parent directory") {
          val foundFile = Directory.findEntryByPath("dir03/../my-file04", root)

          foundFile.map(_.name).head mustEqual file4.name
        }
      }

      describe("when double and single dots is present in the path") {
        it("switches to the parent directory") {
          val foundFile = Directory.findEntryByPath("dir03/../dir03/dir02/./dir00/../my-file03", root)

          foundFile.map(_.name).head mustEqual file3.name
        }
      }

      describe("when entry is not found") {
        it("returns empty") {
          val foundFile = Directory.findEntryByPath("dir03/../dir03/dir00", root)

          foundFile mustBe None
        }
      }

      describe("when path is empty") {
        it("returns the current dir") {
          val entry = Directory.findEntryByPath("", root)

          entry mustBe Some(root)
        }
      }

      describe("when file is in the middle of path") {
        it("returns None") {
          val entry = Directory.findEntryByPath("my-file04/dir03", root)

          entry mustBe None
        }
      }
    }

    describe("resetContents()") {
      it("replaces contents by an empty Vector") {
        Directory.resetContents(root).contents mustEqual Vector()
      }
    }

    describe("updateContents()") {
      it("updates contents deeply nested in the tree") {
        val path = "dir03/../dir03/dir02/./dir00"
        val newDir = Directory("newDir")
        val updatedContents = Directory.addEntry(newDir, dir0).contents
        val updatedRoot = Directory.updateContents(path, updatedContents, root)
        val found = Directory.findEntryByPath(path, updatedRoot).collect { case dir: Directory => dir }

        found.get.contents.map(_.name) must contain (newDir.name)
      }
    }

    describe("getAbsolutePath()") {
      it("gets directory absolute path") {
        val dir02 = Directory.findEntryByPath("/dir03/dir02", root) match {
          case Some(dir: Directory) => dir
          case _ => Directory.empty
        }

        Directory.getAbsolutePath(dir02) mustEqual "/dir03/dir02"
      }
    }
  }
}
