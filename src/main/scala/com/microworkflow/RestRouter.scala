package com.microworkflow

import akka.actor.{Actor, ActorLogging}
import spray.routing.{ExceptionHandler, HttpService, Route, RoutingSettings}
import spray.util.LoggingContext

class RestRouter extends Actor with HttpService with ActorLogging {

  override implicit def actorRefFactory = context

  implicit val routingSettings = RoutingSettings.default(actorRefFactory)

  implicit def myExceptionHandler(implicit log: LoggingContext) = ExceptionHandler.default

  override def receive: Receive = runRoute(route)

  val route: Route = {
    path("") {
      complete(html)
    } ~
    path("ping") {
      get {
        complete("pong")
      }
    }
  }

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
