/*
package utils

import javax.inject.{Inject, Singleton}

import play.api.http.HttpFilters

@Singleton
class Filters @Inject()(
                         loggingFilter: LoggingFilter
                       ) extends HttpFilters {
  val filters = Seq(loggingFilter)
}


import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoggingFilter @Inject()(
                               implicit val mat: Materializer,
                               implicit val ec: ExecutionContext
                             ) extends Filter with Logging {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis
    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      log.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
*/
