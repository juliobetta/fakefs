package app

import files.Directory


case class State(
                  currentDirectory: Option[Directory] = None,
                  source: Directory
                )