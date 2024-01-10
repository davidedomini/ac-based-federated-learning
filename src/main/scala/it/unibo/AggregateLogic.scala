package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class AggregateLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  override def main(): Unit = {
    val initialModel = Seq[Double]() // TODO - this should be the real model :)

    rep(initialModel) { m =>
      val newModel = learn(m)
      val neighborsModels =
        foldhood(Seq[Seq[Double]]())(_ ++ _)(nbr(Seq(newModel)))
      val aggregatedModel = modelsFusion(neighborsModels)
      val eval = evaluate(aggregatedModel)
      //data export
      node.put("Accuracy", eval.accuracy)
      node.put("Loss", eval.loss)
      aggregatedModel
    }
  }

  // TODO - implement {full average, mutual knowledge transfer} algorithm
  private def modelsFusion(models: Seq[Seq[Double]]): Seq[Double] = ???

  // TODO - implement SDG
  private def learn(m: Seq[Double]): Seq[Double] = ???

  // TODO
  private def evaluate(m: Seq[Double]): Evaluation = ???
}
