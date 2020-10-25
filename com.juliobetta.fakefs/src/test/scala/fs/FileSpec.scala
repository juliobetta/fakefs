package fs

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class FileSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach{
  describe("File") {
    val file = File("my-file", None, Some("initial"))

    describe("addContents()") {
      it("replaces the current file contents") {
        val expected = "something"
        val updatedFile = File.addContents(expected, file)

        updatedFile.contents.getOrElse("") mustEqual expected
      }
    }

    describe("appendContents()") {
      it("appends file contents") {
        val updatedFile = File.appendContents(" - something", file)
        val expected = "initial - something"

        updatedFile.contents.getOrElse("") mustEqual expected
      }
    }
  }
}
