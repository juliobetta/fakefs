package command.actions

import app.State
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class CdSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  import fixtures.FileTree._

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
