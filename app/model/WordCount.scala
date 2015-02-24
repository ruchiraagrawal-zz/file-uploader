package model

import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json



/**
 * The below case class is created for handling the requests related to getting the details of the
 * uploaded file. Its constructor has wordMap and countOfWords 
 */
case class WordCount(wordMap: Map[String, Int], countOfWords: Integer)

/**
 * The below method has been written to handle the jsonification
 */
object WordCount {
  implicit val implicitQuoteWrites = new Writes[WordCount] {
    def writes(q: WordCount): JsValue = {
      Json.obj(
        "eachWordOccurence" -> q.wordMap,
        "countOfAllWords" -> q.countOfWords.toString())
    }
  }
}