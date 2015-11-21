package com.microworkflow

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.util.Try

object Boot extends App {

  implicit val actorSystem = ActorSystem()

  val router: ActorRef = actorSystem.actorOf(Props[RestRouter])

  val config = ConfigFactory.defaultApplication()
  val port = Try { config.getInt("http.port")}.getOrElse(8080)
  actorSystem.log.info(s"Binding on port $port")
  IO(Http) ! Http.Bind(router, interface = "0.0.0.0", port = port)

}
