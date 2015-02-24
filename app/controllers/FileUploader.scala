package controllers

import java.util.HashMap
import scala.collection.mutable.ListBuffer
import scala.io.Source
import play.api.libs.json.Json
import play.api.mvc._
import model.FileDetails
import model.WordCount

/**
 * The below controller processes the incoming requests.
 */

object FileUploader extends Controller {

  //The below variable is created so as to get details on every file that has been uploaded
  val filesList = new ListBuffer[FileDetails]()

  /**
   * The below controller takes multipartFormData and removeWord as Query params and returns a JSON
   * response, if the query param removeWord is there then it removes the word from the map which contains it.
   * The json comprises of word map (The counts of each occurrence of a word) and the total word count
   */
  def upload = Action(parse.multipartFormData) {
    implicit request =>
      request.body.file("file").map { file =>
        val fileName = file.filename
        val src = Source.fromFile(file.filename)
        var wordMap = getWordMap(src)
        if (request.body.dataParts.contains("removeWord")) { // This checks if request has removeWord as Query param
          val word = request.body.dataParts.get("removeWord").get.head
          for (key <- wordMap.keys) {
            if (key.contains(word)) {
              wordMap -= key
            }
          }
        }
        filesList += (FileDetails(fileName.toString, wordMap, wordMap.values.sum))	// This adds the uploaded file to filesList list
        Ok(Json.stringify(Json.toJson(WordCount(wordMap, wordMap.values.sum)))) // Used case class here to JSONify the object
      }.getOrElse {
        BadRequest("Invalid Request")
      }
  }

  /**
   * The below method takes BufferedSource and returns a hashmap with word and corresponding occurence
   */
  def getWordMap(src: scala.io.BufferedSource) = {
    src
      .getLines
      .flatMap(_.split("\\s+"))
      .foldLeft(Map.empty[String, Int]) {
        (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
      }
  }

  /**
   * This is a action which works on uploaded files list that can query the system for details on every file that has been uploaded
   */
  def filesUploaded = Action {
    Ok(Json.stringify(Json.toJson(filesList)))
  }

}