package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class ScafiLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  private var model: Seq[Double] = Seq(2.1, 45.5, 1.0, 3.2) //TODO - this should be a real model :)

  override def main(): Unit = {
    val newModel = learn(model)
    val aggregatedModel = foldhoodPlus(newModel)((a,b) => modelsFusion(a,b))(nbr(newModel))
    model = aggregatedModel
    //data export
    node.put("model", model)
    node.put("model size", model.size)
  }

  private def modelsFusion(m1: Seq[Double], m2: Seq[Double]): Seq[Double] = ???
  private def learn(m: Seq[Double]): Seq[Double] = ???

}
