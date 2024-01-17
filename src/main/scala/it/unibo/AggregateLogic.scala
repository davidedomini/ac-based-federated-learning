package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import me.shadaj.scalapy.py
import PythonModules._

class AggregateLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  private val input = 5
  private val hidden = 20
  private val output = 3
  private val epochs = 5
  private val initialModel = SimpleNN(input, hidden, output) // TODO - MNIST CNN

  override def main(): Unit = {

    val (trainDataset, testDataset, dataDivision) = Utils.getDataset(mid()) // TODO - li ricarica ogni volta?

    rep(initialModel) { m =>
      val newModel = learn(m, trainDataset, dataDivision)
      val neighborsModels =
        foldhood(Seq[py.Dynamic]())(_ ++ _)(nbr(Seq(modelSampling(newModel))))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel, testDataset, dataDivision)
      //data export
      node.put("Accuracy", eval.accuracy)
      node.put("Loss", eval.loss)
      aggregatedModel
    }
  }

  private def modelsFusion(models: Seq[py.Dynamic]): py.Dynamic = {
    val w_avg = utils.average_weights(models.toPythonProxy)
    val am = SimpleNN(input, hidden, output) // fresh network
    am.load_state_dict(w_avg)
    am
  }

  private def modelSampling(model: py.Dynamic): py.Dynamic = {
    model.state_dict()
  }

  private def learn(model: py.Dynamic, trainDataset: py.Dynamic, dataDivison: py.Dynamic): py.Dynamic = {
    val trainloader = utils.train_data_loader(trainDataset, dataDivison)
    val newWeights, loss = utils.update_weigths(model, epochs, trainloader, "cpu")
    // TODO - log loss
    val newModel = SimpleNN(input, hidden, output)
    newModel.load_state_dict(newWeights)
    newModel
  }
  
  private def evaluate(model: py.Dynamic, dataset: py.Dynamic, dataDivison: py.Dynamic): Evaluation = {
    val validloader = utils.val_data_loader(dataset, dataDivison)
    val accuracy, loss = utils.evaluate(model, validloader, "cpu")
    Evaluation(1.0, 0.0) // TODO - understand how to convert type of accuracy and loss from Dynamic to Double
  }

}
