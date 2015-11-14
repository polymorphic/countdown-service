package com.microworkflow

import akka.actor.{ActorLogging, Actor}
import spray.routing.{ExceptionHandler, Route, RoutingSettings, HttpService}
import spray.util.LoggingContext

class RestRouter extends Actor with HttpService with ActorLogging {

  override implicit def actorRefFactory = context

  implicit val routingSettings = RoutingSettings.default(actorRefFactory)

  implicit def myExceptionHandler(implicit log: LoggingContext) = ExceptionHandler.default

  override def receive: Receive = runRoute(route)

  val route: Route = {
    path("ping") {
      get {
        complete("pong")
      }
    }
  }

}
