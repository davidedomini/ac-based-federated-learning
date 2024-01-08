package it.unibo

import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class ScafiLogic
  extends AggregateProgram
    with StandardSensors
    with ScafiAlchemistSupport
    with FieldUtils {

  override def main(): Any = {
    val c = foldhoodPlus(0)((a,b) => a + b)(nbr(1))
    node.put("count", c)
  }
}
