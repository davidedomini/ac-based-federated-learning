package it.unibo.mnist

import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.PyQuote
import PythonModules._


object MNISTNN {
  private val autodiffDevice =
    if (torch.cuda.is_available().as[Boolean]) {
      "cuda"
    } else {
      "cpu"
    }

  def apply(input: Int, output: Int): py.Dynamic = {

    val reshapingStrategy = (x: py.Dynamic) => {
      val s1 = py"$x.shape[1]"
      val s2 = py"$x.shape[2]"
      val s3 = py"$x.shape[3]"
      x.view(-1, s1 * s2 * s3)
    }

    nn
      .Sequential(
        nn.Conv2d(input, 10, kernel_size = 5).to(autodiffDevice),
        nn.Functional.max_pool2d(kernel_size = 2).to(autodiffDevice),
        nn.Functional.relu().to(autodiffDevice),
        nn.Conv2d(10, 20, kernel_size = 5).to(autodiffDevice),
        nn.conv2_drop().to(autodiffDevice),
        nn.Functional.max_pool2d(kernel_size = 2).to(autodiffDevice),
        nn.Functional.relu().to(autodiffDevice),
        torchvision.transforms.Lambda(reshapingStrategy),
        nn.Linear(320, 50).to(autodiffDevice),
        nn.Functional.relu().to(autodiffDevice),
        nn.Functional.dropout(training = py"self.training").to(autodiffDevice),
        nn.Linear(50, output).to(autodiffDevice),
        nn.Functional.log_softmax(dim = 1).to(autodiffDevice)
      ).to(autodiffDevice)
  }
}
