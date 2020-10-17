package playground

import command.Command
import app.State
import files.{Directory, File}

object Playground extends App {
  val file1 = File("my-file01", Some("I have something 01"))
  val file2 = File("my-file02", Some("I have something 02"))
  val file3 = File("my-file03", Some("I have something 03"))
  val file4 = File("my-file04", Some("I have something 04"))
  val file5 = File("my-file05", Some("I have something 05"))

  val dir1 = Directory("dir01", List(file1, file2))
  val dir2 = Directory("dir02", List(file3))
  val dir3 = Directory("dir03", List(dir1, dir2))

  val root = Directory("root", List(dir3, file4, file5))

//  root.printFilesTree()

  val state = State()
  val (newState, output) = Command.from("cd some/path")(state)

  output match {
    case Some(output) => println(output)
    case None => println("empty")
  }
}

