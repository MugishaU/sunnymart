package config

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.dynamodbv2.{
  AmazonDynamoDB,
  AmazonDynamoDBClientBuilder
}
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import play.api.libs.json._

import scala.collection.immutable.HashMap
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object DynamoDb {
  val dynamoDbClient: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient

  private def createError[T](
      errorMessage: String,
      statusCode: Int
  ): Either[Map[String, String], T] = {
    val error =
      Map("errorMessage" -> errorMessage, "statusCode" -> statusCode.toString)
    Left(error)
  }

  def getItem[T](
      primaryKeyName: String,
      primaryKeyValue: String,
      sortKeyName: Option[String] = None,
      sortKeyValue: Option[String] = None,
      tableName: String
  )(implicit reads: Reads[T]): Either[Map[String, String], T] = {

    val key = (sortKeyName, sortKeyValue) match {
      case (Some(keyName), Some(keyValue)) =>
        HashMap(
          primaryKeyName -> new AttributeValue(primaryKeyValue),
          keyName -> new AttributeValue(keyValue)
        ).asJava
      case (_, _) =>
        HashMap(
          primaryKeyName -> new AttributeValue(primaryKeyValue)
        ).asJava
    }

    val request =
      new GetItemRequest()
        .withKey(key)
        .withTableName(tableName)

    val item = Try(Option(dynamoDbClient.getItem(request).getItem))

    item match {
      case Failure(e: AmazonServiceException) =>
        println(e)
        createError(e.getErrorMessage, e.getStatusCode)

      case Failure(_) =>
        createError("Unknown error on database fetch", 500)

      case Success(None) =>
        createError("No such item found", 404)

      case Success(Some(value)) =>
        val jsonString = ItemUtils.toItem(value).toJSON
        val awsItem = Try(Json.parse(jsonString).as[T])

        awsItem match {
          case Failure(exception) =>
            println(exception)
            createError("Failure to parse DynamoDB item", 500)
          case Success(awsItem) => Right(awsItem)
        }
    }
  }
}
