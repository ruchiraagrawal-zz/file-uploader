package scala

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.File
import scala.language.implicitConversions
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.http._
import play.api.http.Writeable
import play.api.libs.Files.TemporaryFile
import play.api.mvc.Codec
import play.api.mvc._
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.WithApplication
import play.api.test.{ FakeApplication, FakeRequest }
import controllers.FileUploader
import scala.io.Source
import play.Play
import play.api.test.FakeHeaders

trait FakeMultipartUpload {
  implicit def writeableOf_multiPartFormData(implicit codec: Codec): Writeable[MultipartFormData[TemporaryFile]] = {
    val builder = MultipartEntityBuilder.create().setBoundary("12345678")

    def transform(multipart: MultipartFormData[TemporaryFile]): Array[Byte] = {
      multipart.dataParts.foreach { part =>
        part._2.foreach { p2 =>
          builder.addPart(part._1, new StringBody(p2, ContentType.create("text/plain", "UTF-8")))
        }
      }
      multipart.files.foreach { file =>
        val part = new FileBody(file.ref.file, ContentType.create(file.contentType.getOrElse("application/octet-stream")), file.filename)
        builder.addPart(file.key, part)
      }

      val outputStream = new ByteArrayOutputStream
      builder.build.writeTo(outputStream)
      outputStream.toByteArray
    }

    new Writeable[MultipartFormData[TemporaryFile]](transform, Some(builder.build.getContentType.getValue))
  }

  def fileUpload(key: String, file: File, contentType: String): MultipartFormData[TemporaryFile] = {
    MultipartFormData(
      dataParts = Map(),
      files = Seq(FilePart[TemporaryFile](key, file.getName, Some(contentType), TemporaryFile(file))),
      badParts = Seq(),
      missingFileParts = Seq())
  }

  /** shortcut for a request body containing a single file attachment */
  case class WrappedFakeRequest[A](fr: FakeRequest[A]) {
    def withFileUpload(key: String, file: File, contentType: String) = {
      fr.withBody(fileUpload(key, file, contentType))
    }
  }
  implicit def toWrappedFakeRequest[A](fr: FakeRequest[A]) = WrappedFakeRequest(fr)
}

class FileUploaderSpec extends Specification with FakeMultipartUpload {

  "File" should {
        "be uploaded and return OK status" in {
          running(FakeApplication()) {
            val basePath: String = Play.application.path.getCanonicalPath()
            val uploadFile = new File(basePath + "/test-data/name.txt")
            val req = FakeRequest(POST, "/file/upload").
              withFileUpload("file", uploadFile, "text/plain")
            val response = route(req).get
            status(response) must equalTo(OK)
          }
        }

    "be uploaded and return the correct word count" in new WithApplication {
      val basePath: String = Play.application.path.getCanonicalPath()
      val uploadFile = new File(basePath + "/test-data/name.txt")
      val result = FileUploader.getWordMap(Source.fromFile(uploadFile))
      result.values.sum must beEqualTo(601)
    }

    "be uploaded and return the correct occurences" in new WithApplication {
      val basePath: String = Play.application.path.getCanonicalPath()
      val uploadFile = new File(basePath + "/test-data/name.txt")
      val result = FileUploader.getWordMap(Source.fromFile(uploadFile))
      result.get("Scala") must beEqualTo(Some(13))
    }

  }

}