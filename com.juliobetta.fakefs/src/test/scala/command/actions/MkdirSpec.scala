package command.actions

import app.State
import fs.Directory
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class MkdirSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  describe("Mkdir") {
    import fixtures.FileTree._

    val initialState = State("dir03/dir02", root)

    it("creates a new directory and updates state") {
      val dirName = "newDir"
      val (state, _) = Mkdir(Vector(dirName))(initialState)
      val found = Directory.findEntryByPath(state.currentPath, state.root).collect { case dir: Directory => dir }

      found.get.contents.map(_.name) must contain (dirName)
      state.currentDirectory.contents.map(_.name) must contain (dirName)
    }

    describe("when passing in -p") {
      ignore("create a new directory in the given path") {

      }

      describe("when path is not found") {
        ignore("returns an error message and keeps the same state") {

        }
      }
    }
  }
}
