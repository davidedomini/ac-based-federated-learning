package it.unibo

import me.shadaj.scalapy.py

object SimpleNN {

  private val nn = py.module("torch.nn")
  private val torch = py.module("torch")
  private val autodiffDevice =
    if (torch.cuda.is_available().as[Boolean]) {"cuda"} else {"cpu"}

  def apply(input: Int, hidden: Int, output: Int): py.Dynamic = {
    nn
      .Sequential(
        nn.Linear(input, hidden).to(autodiffDevice),
        nn.ReLU().to(autodiffDevice),
        nn.Linear(hidden, hidden).to(autodiffDevice),
        nn.ReLU().to(autodiffDevice),
        nn.Linear(hidden, output).to(autodiffDevice),
      )
      .to(autodiffDevice)
  }
}
