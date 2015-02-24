package model

import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 * The below case class is created for handling the requests related to getting the details of all the
 * uploaded files. Its constructor has FileName, wordMap and countOfWords 
 */
case class FileDetails(fileName: String, wordMap: Map[String, Int], countOfWords: Integer)


/**
 * The below method has been written to handle the jsonification
 */
object FileDetails {
  implicit val implicitQuoteWrites = new Writes[FileDetails] {
    def writes(q: FileDetails): JsValue = {
      Json.obj(
        "fileName" -> q.fileName,
        "eachWordOccurence" -> q.wordMap,
        "countOfAllWords" -> q.countOfWords.toString())
    }
  }
}
