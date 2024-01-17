package it.unibo

import PythonModules._
import me.shadaj.scalapy.py

object Utils {

  def getDataset(id: Int) = {
    val apply_transform = torchvision.transforms.toTensor()
    val trainDataset = torchvision
      .datasets
      .MNIST(
        "data/mnist/",
        train = true,
        download=true,
        transform=apply_transform
      )

    val testDataset =torchvision
      .datasets
      .MNIST(
        "data/mnist/",
        train = true,
        download = false,
        transform = apply_transform
      )

    val dataDivision = utils.load_data_division("data/data_division.json", id)

    (trainDataset, testDataset, dataDivision)
  }

  // TODO
  def trainingDataLoader(): py.Dynamic = ???

}
