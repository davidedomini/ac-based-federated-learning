package it.unibo

import me.shadaj.scalapy.py

object PythonModules {
  val nn: py.Module = py.module("torch.nn")
  val torch: py.Module = py.module("torch")
  val utils: py.Module = py.module("FLutils")
  val torchvision: py.Module = py.module("torchvision")
  val log = py.module("torch.utils.tensorboard")
}
