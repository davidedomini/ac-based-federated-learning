package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import me.shadaj.scalapy.py

class AggregateLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  override def main(): Unit = {
    val initialModel = SimpleNN(5, 20, 3) // TODO

    rep(initialModel) { m =>
      val newModel = learn(m)
      val neighborsModels =
        foldhood(Seq[py.Dynamic]())(_ ++ _)(nbr(Seq(newModel.state_dict())))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel)
      //data export
      node.put("Accuracy", eval.accuracy)
      node.put("Loss", eval.loss)
      aggregatedModel
    }
  }

  private def modelsFusion(models: Seq[py.Dynamic]): py.Dynamic = {
    val summedModels = models.reduce(elementByElementSum)
    val averageModel = avgModel(summedModels, models.size)
    averageModel
  }

  // TODO
  private def elementByElementSum(m1: py.Dynamic, m2: py.Dynamic): py.Dynamic = ???
  private def avgModel(m: py.Dynamic, k: Int): py.Dynamic = ???
  
  // TODO - implement SDG
  private def learn(m: py.Dynamic): py.Dynamic = ???

  // TODO
  private def evaluate(m: py.Dynamic): Evaluation = ???
}
