package db

import com.amazonaws.services.dynamodbv2.model.AttributeValue

import java.util
import scala.jdk.CollectionConverters._

case class FilterExpression(
    filterKey: String,
    filterValue: String,
    filterOperator: FilterOperator
) {
  override def toString: String = {
    s"$filterKey ${filterOperator.value} :$filterKey"
  }

  def attributeValue: util.Map[String, AttributeValue] = {
    Map(s":$filterKey" -> new AttributeValue(filterValue)).asJava
  }
}

sealed trait FilterOperator {
  def value: String
}

case object Equals extends FilterOperator {
  val value = "="
}
case object GreaterThan extends FilterOperator {
  val value = ">"
}
case object LessThan extends FilterOperator {
  val value = "<"
}
