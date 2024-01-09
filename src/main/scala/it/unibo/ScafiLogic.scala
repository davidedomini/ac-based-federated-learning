package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class ScafiLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  override def main(): Unit = {
    val initialModel = Seq(mid().toDouble) // TODO - this should be a real model :)
    rep(initialModel) { m =>
      val newModel = learn(m)
      val aggregatedModel = foldhoodPlus(newModel)((a,b) => modelsFusion(a,b))(nbr(newModel))
      val eval = evaluate(aggregatedModel)
      //data export
      node.put("Accuracy", eval.accuracy)
      node.put("Loss", eval.loss)
      aggregatedModel
    }
  }

  // TODO - implement {full average, mutual knowledge transfer} algorithm
  private def modelsFusion(m1: Seq[Double], m2: Seq[Double]): Seq[Double] = m1 ++ m2

  // TODO - implement SDG
  private def learn(m: Seq[Double]): Seq[Double] = m

  // TODO
  private def evaluate(m: Seq[Double]): Evaluation = ???
}