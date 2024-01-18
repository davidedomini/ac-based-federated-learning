package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.PyQuote
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
  private val initialModel = MNISTNN(1, 10)

  override def main(): Unit = {

    val (trainDataset, testDataset, dataDivision) = Utils.getDataset(mid()) // TODO - could be a performance issue

    rep(initialModel) { m =>
      val newModel = learn(m, trainDataset, dataDivision)
      val neighborsModels =
        foldhood(Seq[py.Dynamic]())(_ ++ _)(nbr(Seq(modelSampling(newModel))))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel, testDataset, dataDivision)
      //data export
      // TODO - maybe better data could be exported
      val accuracy = py"$eval[0]"
      val loss = py"$eval[1]"
      node.put("Accuracy", accuracy)
      node.put("Loss", loss)
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
    val result = utils.update_weigths(model, epochs, trainloader, "cpu") // result = (newWeights, loss)
    val newWeights = py"$result[0]"
    val loss = py"$result[1]"
    // TODO - log loss
    val newModel =  MNISTNN(1, 10)
    newModel.load_state_dict(newWeights)
    newModel
  }
  
  private def evaluate(model: py.Dynamic, dataset: py.Dynamic, dataDivison: py.Dynamic): py.Dynamic = {
    val validloader = utils.val_data_loader(dataset, dataDivison)
    val evaluationResult = utils.evaluate(model, validloader, "cpu")
    evaluationResult
  }

}
