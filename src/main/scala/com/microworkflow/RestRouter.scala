package com.microworkflow

import akka.actor.{Actor, ActorLogging}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, HttpService, Route, RoutingSettings}
import spray.util.LoggingContext

import scala.util.{Failure, Success}

object RestRouter {
  val html = <html lang="en">
    <head>
      <meta http-equiv="content-type" content="text/html; charset=utf-8" />
      <title>Title Goes Here</title>
    </head>
    <body>
      <p>Literal XML to the rescue suckers!</p>
    </body>
  </html>
}

class RestRouter extends Actor with HttpService with ActorLogging {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override implicit def actorRefFactory = context
  implicit val routingSettings = RoutingSettings.default(actorRefFactory)

  implicit def myExceptionHandler(implicit log: LoggingContext) = ExceptionHandler( {
    case iae: IllegalArgumentException ⇒ ctx ⇒ {
      log.warning("Exception thrown while parsing argment")
      ctx.complete(InternalServerError)
    }
  })

  override def receive: Receive = runRoute(route)

  val route: Route = {
    path("") {
      complete(RestRouter.html)
    } ~
    path("ping") {
      get {
        complete("pong")
      }
    } ~
    path("register") {
      post {
        entity(as[String]) { jsonString =>
          val json = mapper.readTree(jsonString)
          val timeStr: String = json.get("time").asText()
          TimeCalculator.parseTime(timeStr) match {
            case Success(dt) ⇒
              val q = TimeCalculator.formatTime(dt)
              complete(q)
            case Failure(t) =>
              failWith(new IllegalArgumentException(t))
          }
        }
      }
    }
  }
}
