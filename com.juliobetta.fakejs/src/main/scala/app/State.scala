package app

import files.{Directory, FileEntry}


case class State(
                  currentDirectory: Option[Directory] = None,
                  data: List[FileEntry] = Nil
                )