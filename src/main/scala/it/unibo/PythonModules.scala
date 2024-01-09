package it.unibo

import me.shadaj.scalapy.py

object PythonModules {
  val nn: py.Module = py.module("torch.nn")
  val torch: py.Module = py.module("torch")
}
