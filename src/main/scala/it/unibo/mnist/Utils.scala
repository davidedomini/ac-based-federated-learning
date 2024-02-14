package it.unibo.mnist

import it.unibo.common.PythonModules._

object Utils {

  def getDataset(id: Int) = {
    val apply_transform = torchvision.transforms.ToTensor()
    val trainDataset = torchvision
      .datasets
      .MNIST(
        "data/mnist/",
        train = true,
        download = true,
        transform = apply_transform
      )

    val testDataset = torchvision
      .datasets
      .MNIST(
        "data/mnist/",
        train = true,
        download = false,
        transform = apply_transform
      )

    val dataDivision = utils.load_data_division("data/data_division_iid.json", id)

    (trainDataset, testDataset, dataDivision)
  }

}
