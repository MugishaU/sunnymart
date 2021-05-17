package config

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import controllers.Helpers.getItemCategory
import model.aws.AwsItem
import model.domain.Item
import play.api.libs.json._

import scala.collection.immutable.HashMap
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object DynamoDb {

  def getItem(
      primaryKeyName: String,
      primaryKeyValue: String,
      tableName: String
  ): Option[Item] = {
    val dynamoDbClient = AmazonDynamoDBClientBuilder.defaultClient
    val key = HashMap(
      primaryKeyName -> new AttributeValue(primaryKeyValue)
    ).asJava

    val request =
      new GetItemRequest()
        .withKey(key)
        .withTableName(tableName)

    val item = Try(Option(dynamoDbClient.getItem(request).getItem))

    item match {
      case Failure(e: AmazonServiceException) =>
        println(e.getErrorMessage)
        None

      case Failure(_) =>
        println("Unknown Error")
        None

      case Success(None) =>
        println("No Item")
        None
      case Success(Some(value)) =>
        val jsonString = ItemUtils.toItem(value).toJSON
        val triedAwsItem = Try(Json.parse(jsonString).as[AwsItem])

        triedAwsItem match {
          case Failure(exception) =>
            println(s"Failure to parse DynamoDb item\n\nFull Error: $exception")
            None
          case Success(awsItem) =>
            val maybeCategory = getItemCategory(awsItem.category)
            maybeCategory match {
              case Some(category) =>
                Some(
                  Item(
                    id = awsItem.id,
                    name = awsItem.name,
                    category = category,
                    details = awsItem.details,
                    price = awsItem.price,
                    essentialStatus = awsItem.essentialStatus,
                    quantity = awsItem.quantity
                  )
                )
              case None =>
                println("No such item category")
                None
            }
        }
    }
  }
}
