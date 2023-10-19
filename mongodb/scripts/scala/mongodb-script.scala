import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document
import scala.concurrent.Await
import scala.concurrent.duration._

object MongoInteract extends App {
  val mongoClient: MongoClient = MongoClient("mongodb://username:password@localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("CR_Database")
  val collection: MongoCollection[Document] = database.getCollection("CR_Table")

  Await.result(collection.drop().toFuture(), 10.seconds)

  Await.result(collection.insertOne(Document("_id" -> 1, "name" -> "William", "age" -> 56)).toFuture(), 10.seconds)
  Await.result(collection.insertOne(Document("_id" -> 2, "name" -> "Bob", "age" -> 25)).toFuture(), 10.seconds)

  Await.result(collection.insertOne(Document(
    "_id" -> 3,
    "name" -> "Charlie",
    "age" -> 35,
    "address" -> Document(
      "street" -> "5 rue de Cassandra",
      "city" -> "NoSQL",
      "postal_code" -> "87000"
    )
  )).toFuture(), 10.seconds)

  println("Data sorted ascending by age:")
  val resultsAsc = Await.result(collection.find().sort(Document("age" -> 1)).toFuture(), 10.seconds)
  resultsAsc.foreach(doc => println(doc.toJson()))

  println("Data sorted descending by age:")
  val resultsDesc = Await.result(collection.find().sort(Document("age" -> -1)).toFuture(), 10.seconds)
  resultsDesc.foreach(doc => println(doc.toJson()))

  mongoClient.close()
}
