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
      val expected = s"$dir3\n$file4\n$file5"

      output.getOrElse("") must include (expected)
    }

    describe("when path is not found") {
      it("returns an error message") {
        val (_, output) = Ls(Vector("unknown/path"))(initialState)

        output.getOrElse("") must include ("no such directory")
      }
    }
  }
}
