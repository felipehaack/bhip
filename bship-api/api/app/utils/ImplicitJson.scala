package utils

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Json.JsValueWrapper

object ImplicitJson {

  implicit val tupleWrites = new Writes[(String, String)] {
    def writes(input: (String, String)): JsValue = {
      Json.obj(input._1 -> input._2)
    }
  }

  implicit val tupleReads = new Reads[(String, String)] {
    def reads(js: JsValue): JsResult[(String, String)] = {
      JsSuccess(js match {
        case JsObject(fields) => (fields.head._1, fields.head._2.as[String])
        case _ => ("", "")
      })
    }
  }

  implicit val seqTupleWrites = new Writes[Seq[(String, String)]] {
    def writes(input: Seq[(String, String)]): JsValue = {
      Json.obj(input.map {
        case (key, value) => {
          val result: (String, JsValueWrapper) = key.toString -> value.toString
          result
        }
      }: _*)
    }
  }

  implicit val seqTupleReads = new Reads[Seq[(String, String)]] {
    def reads(js: JsValue): JsResult[Seq[(String, String)]] = {
      JsSuccess(js match {
        case JsObject(fields) => fields.map(a => (a._1, a._2.as[String])).toList
        case _ => List()
      })
    }
  }
}
