package it.unibo

import me.shadaj.scalapy.py
import PythonModules._

object MNISTNN {
  private val autodiffDevice =
    if (torch.cuda.is_available().as[Boolean]) { "cuda" } else { "cpu" }

  def apply(input: Int, hidden: Int, output: Int): py.Dynamic = {
    nn
      .Sequential(
        nn.Conv2d(input, 10, kernel_size=5).to(autodiffDevice),
        nn.Functional.relu(nn.Functional.max_pool2d(2)).to(autodiffDevice),
        nn.Conv2d(10, 20, kernel_size=5).to(autodiffDevice),
        nn.Functional.relu(nn.Functional.max_pool2d(2))
      )
      .to(autodiffDevice)
  }
}
