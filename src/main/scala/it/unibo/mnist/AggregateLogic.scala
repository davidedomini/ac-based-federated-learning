package it.unibo.mnist

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import it.unibo.common.PythonModules._
import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.PyQuote

class AggregateLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  private val input = 5
  private val hidden = 20
  private val output = 3
  private val epochs = 5
  private val initialModel = utils.mnist_cnn_factory()

  override def main(): Unit = {

    val (trainDataset, testDataset, dataDivision) = Utils.getDataset(mid())

    rep((initialModel, 0)) { p =>
      val m = p._1
      val actualTick = p._2
      val (newModel, trainLoss) = learn(m, trainDataset, dataDivision)
      val neighborsModels =
        foldhood(Seq[py.Dynamic]())(_ ++ _)(nbr(Seq(modelSampling(newModel))))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel, testDataset, dataDivision)
      //data export
      val accuracy = py"$eval[0]"
      val loss = py"$eval[1]"
      node.put("TrainLoss", trainLoss.as[Double])
      node.put("Accuracy", accuracy.as[Double])
      node.put("Loss", loss.as[Double])
      snapshot(aggregatedModel, actualTick, mid())
      (aggregatedModel, actualTick+1)
    }
  }

  private def modelsFusion(models: Seq[py.Dynamic]): py.Dynamic = {
    val w_avg = utils.average_weights(models.toPythonProxy)
    val am = utils.mnist_cnn_factory() // fresh network
    am.load_state_dict(w_avg)
    am
  }
 
  private def modelSampling(model: py.Dynamic): py.Dynamic = {
    model.state_dict()
  }

  private def learn(model: py.Dynamic, trainDataset: py.Dynamic, dataDivison: py.Dynamic): (py.Dynamic, py.Dynamic) = {
    val trainloader = utils.train_data_loader(trainDataset, dataDivison)
    val result = utils.update_weights(model, epochs, trainloader, "cpu") // result = (newWeights, loss)
    val newWeights = py"$result[0]"
    val loss = py"$result[1]"
    val newModel = utils.mnist_cnn_factory() // fresh network
    newModel.load_state_dict(newWeights)
    (newModel, loss)
  }
  
  private def evaluate(model: py.Dynamic, dataset: py.Dynamic, dataDivison: py.Dynamic): py.Dynamic = {
    val validloader = utils.val_data_loader(dataset, dataDivison)
    val evaluationResult = utils.evaluate(model, validloader, "cpu")
    evaluationResult
  }

  private def snapshot(model: py.Dynamic, actualTick: Int, id: Int): Unit = {
    torch.save(
      model.state_dict(),
      s"networks/agent$id-network$actualTick"
    )
  }

}
