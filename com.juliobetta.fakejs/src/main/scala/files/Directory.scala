package files

import scala.annotation.tailrec

case class Directory(override val name: String, contents: List[FileEntry] = Nil) extends FileEntry(name) {
//  def printFilesTree(): Unit = {
//    @tailrec
//    def output(entries: List[FileEntry], acc: List[String]): List[String] =
//      entries match {
//        case Nil => acc
//        case (dir: Directory) :: tail => output(dir.contents ++ tail, acc :+ s"${dir}")
//        case head :: tail => output(tail, acc :+ s"${head}")
//      }
//
//    output(contents, Nil).foreach { println }
//  }
}
