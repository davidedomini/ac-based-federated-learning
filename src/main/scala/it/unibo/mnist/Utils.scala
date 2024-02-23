package it.unibo.mnist

import it.unibo.common.PythonModules._
import me.shadaj.scalapy.py

object Utils {

  def getDataset(id: Int, experiment: Int) = {
    val apply_transform = torchvision.transforms.ToTensor()
    var trainDataset: py.Dynamic = null
    var testDataset: py.Dynamic = null
    if(experiment == 0){
      trainDataset = torchvision
        .datasets
        .MNIST(
          "data/mnist/",
          train = true,
          download = true,
          transform = apply_transform
        )

      testDataset = torchvision
        .datasets
        .MNIST(
          "data/mnist/",
          train = true,
          download = true,
          transform = apply_transform
        )
    } else {
      trainDataset = torchvision
        .datasets
        .FashionMNIST(
          "data/mnist/",
          train = true,
          download = true,
          transform = apply_transform
        )

      testDataset = torchvision
        .datasets
        .FashionMNIST(
          "data/mnist/",
          train = true,
          download = true,
          transform = apply_transform
        )
    }

    val dataDivision = utils.load_data_division("data/data_division_iid.json", id)
    (trainDataset, testDataset, dataDivision)
  }

}
