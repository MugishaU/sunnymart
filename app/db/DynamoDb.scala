package db

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.dynamodbv2.{
  AmazonDynamoDB,
  AmazonDynamoDBClientBuilder
}
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.{
  AttributeValue,
  GetItemRequest,
  ScanRequest
}
import play.api.libs.json._

import scala.collection.immutable.HashMap
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object DynamoDb {
  type DynamoDbResponse[T] = Either[Map[String, String], T]
  type DynamoDbKeys = java.util.Map[String, AttributeValue]
  type DynamoDbRequest = java.util.Map[String, AttributeValue]
  val dynamoDbClient: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient

  final case class PrimaryKey(name: String, value: String)
  final case class SortKey(name: String, value: String)

  private def createError[T](
      errorMessage: String,
      statusCode: Int
  ): DynamoDbResponse[T] = {
    val error =
      Map("errorMessage" -> errorMessage, "statusCode" -> statusCode.toString)
    Left(error)
  }

  private def createDynamoKey(
      primaryKey: PrimaryKey,
      sortKey: Option[SortKey]
  ): DynamoDbKeys = {
    sortKey match {
      case Some(sortKey) =>
        HashMap(
          primaryKey.name -> new AttributeValue(primaryKey.value),
          sortKey.name -> new AttributeValue(sortKey.value)
        ).asJava
      case None =>
        HashMap(
          primaryKey.name -> new AttributeValue(primaryKey.value)
        ).asJava
    }
  }

  private def sendDynamoDbRequest(
      key: DynamoDbKeys,
      tableName: String
  ): Try[Option[DynamoDbRequest]] = {
    val request =
      new GetItemRequest()
        .withKey(key)
        .withTableName(tableName)
    Try(Option(dynamoDbClient.getItem(request).getItem))
  }

  def parseItem[T](value: DynamoDbRequest)(implicit reads: Reads[T]): Try[T] = {
    val jsonString = ItemUtils.toItem(value).toJSON
    Try(Json.parse(jsonString).as[T])
  }

  private def convertDynamoDbResponse[T](
      request: Try[Option[DynamoDbRequest]]
  )(implicit reads: Reads[T]): DynamoDbResponse[T] = {
    request match {
      case Failure(e: AmazonServiceException) =>
        createError(e.getErrorMessage, e.getStatusCode)

      case Failure(_) =>
        createError("Unknown error on database fetch", 500)

      case Success(None) =>
        createError("No such item found", 404)

      case Success(Some(value)) =>
        val awsItem = parseItem(value)

        awsItem match {
          case Failure(_) =>
            createError("Failure to parse DynamoDB item", 500)
          case Success(awsItem) => Right(awsItem)
        }
    }
  }

  def getDynamoItem[T](
      primaryKey: PrimaryKey,
      sortKey: Option[SortKey] = None,
      tableName: String
  )(implicit reads: Reads[T]): DynamoDbResponse[T] = {

    val key = createDynamoKey(primaryKey, sortKey)
    val dynamoRequest = sendDynamoDbRequest(key, tableName)
    convertDynamoDbResponse(dynamoRequest)

  }

  def scanDynamoTable[T](
      tableName: String,
      filterExpression: Option[FilterExpression] = None
  )(implicit reads: Reads[T], writes: OWrites[T]): DynamoDbResponse[List[T]] = {

    val scanRequest = filterExpression match {
      case Some(filterExpression) =>
        new ScanRequest()
          .withTableName(tableName)
          .withFilterExpression(filterExpression.toString)
          .withExpressionAttributeValues(filterExpression.attributeValue)
      case None =>
        new ScanRequest()
          .withTableName(tableName)
    }

    val request = Try(Option(dynamoDbClient.scan(scanRequest)))

    request match {
      case Failure(e: AmazonServiceException) =>
        createError(e.getErrorMessage, e.getStatusCode)

      case Failure(_) =>
        createError("Unknown error on database fetch", 500)

      case Success(None) =>
        createError("No such item found", 404)

      case Success(Some(result)) =>
        val awsItemList = result.getItems.asScala
          .map(parseItem[T])
          .toList

        val (awsSuccessList, awsFailureList) =
          awsItemList.partition(_.isSuccess)

        if (awsFailureList.nonEmpty) {
          println(s"Failures: $awsFailureList")
        }
        Right(awsSuccessList.map(_.get))
    }

  }
}
