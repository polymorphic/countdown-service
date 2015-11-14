package com.microworkflow

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.io.IO
import spray.can.Http

object Boot extends App {

  implicit val actorSystem = ActorSystem()

  val router: ActorRef = actorSystem.actorOf(Props[RestRouter])

  val port = sys.env.get("PORT").map(_.toInt).getOrElse(8080)

  actorSystem.log.info(s"Binding on port $port")

  IO(Http) ! Http.Bind(router, interface = "0.0.0.0", port = port)

}
