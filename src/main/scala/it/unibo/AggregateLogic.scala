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

  override def main(): Unit = {
    val initialModel = SimpleNN(input, hidden, output) // TODO

    rep(initialModel) { m =>
      val newModel = learn(m)
      val neighborsModels =
        foldhood(Seq[py.Dynamic]())(_ ++ _)(nbr(Seq(modelSampling(newModel))))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel)
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

  // TODO - implement SDG
  private def learn(m: py.Dynamic): py.Dynamic = m

  // TODO
  private def evaluate(m: py.Dynamic): Evaluation = Evaluation(1.0, 0.0)

}
