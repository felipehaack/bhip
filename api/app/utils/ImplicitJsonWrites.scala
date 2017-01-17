package utils

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsValue, Json, Writes}

object ImplicitJsonWrites {

  implicit val tuple = new Writes[(String, String)] {
    def writes(input: (String, String)): JsValue = {
      Json.obj(input._1 -> input._2)
    }
  }

  implicit val seqWithTuple = new Writes[Seq[(String, String)]] {
    def writes(input: Seq[(String, String)]): JsValue = {
      Json.obj(input.map {
        case (key, value) => {
          val result: (String, JsValueWrapper) = key.toString -> value.toString
          result
        }
      }: _*)
    }
  }
}
