package command.actions

import app.State
import fs.Directory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class LsSpec extends AnyFunSpec with Matchers {
  describe("Ls") {
    import fixtures.FileTree._

    val initialState: State = State(Directory.ROOT_PATH, root)

    it("prints the list of entries from a directory") {
      val (_, output) = Ls(Vector())(initialState)

      output.getOrElse("").split("\n") must contain theSameElementsAs Vector(dir3.name, file4.name, file5.name)
    }

    describe("when path is not found") {
      it("returns an error message") {
        val (_, output) = Ls(Vector("unknown/path"))(initialState)

        output.getOrElse("") must include ("no such directory")
      }
    }
  }
}
