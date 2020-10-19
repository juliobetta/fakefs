package app

import fs.Directory


case class State(
                  currentDirectory: Directory,
                  source: Directory
                )