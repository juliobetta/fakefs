package command.actions

import app.State
import fs.Directory
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class CdSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  val dir0: Directory = Directory("dir00")
  val dir1: Directory = Directory("dir01")
  val dir2: Directory = Directory.addEntries(Vector(dir0))(Directory("dir02"))
  val dir3: Directory = Directory.addEntries(Vector(dir1, dir2))(Directory("dir03"))

  val root: Directory = Directory.addEntries(Vector(dir3))(Directory.empty)

  val initialState: State = State(root, root)

  describe("Cd") {
    it("finds directory by path and update state") {
      val path = "dir03/dir02/dir00"
      val (state, _) = Cd(Vector(path))(initialState)

      state.currentDirectory.name mustEqual dir0.name
    }

    describe("when directory is not found") {
      it("returns an error message and keeps the same state") {
        val path = "unknown/path"
        val (state, output) = Cd(Vector(path))(initialState)

        state mustEqual initialState
        output.getOrElse("") must include regex "no such directory"
      }
    }
  }
}
