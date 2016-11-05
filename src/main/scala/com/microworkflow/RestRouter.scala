package com.microworkflow

import akka.actor.{Actor, ActorLogging, Props}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.microworkflow.SkillHandler.HandleSkill
import spray.http.StatusCodes._
import spray.routing._
import spray.util.LoggingContext

import scala.util.{Failure, Success, Try}

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
      get {
        complete(RestRouter.html)
      } ~
      post {
        entity(as[String]) { jsonString =>
          Try { mapper.readTree(jsonString) } match {
            case scala.util.Success(jsonNode) ⇒
              log.debug(s"processing $jsonString")
              foo(jsonNode)
            case scala.util.Failure(t) ⇒
              log.error(t, "error parsing document body")
              failWith(t)
          }

        }
      }
    } ~
    path("ping") {
      get {
        complete("pong")
      }
    } ~
    path("info") {
      get {
        complete(buildinfo.BuildInfo.toJson)
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

  def foo(jsonNode: JsonNode): Route = {
    rc: RequestContext ⇒ {
      val requestHandler = context.actorOf(SkillHandler.props(rc))
      requestHandler ! HandleSkill(jsonNode)
    }
  }
}

class SkillHandler(rc: RequestContext) extends Actor with ActorLogging {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def receive: Actor.Receive = {
    case HandleSkill(jsonNode) ⇒
      rc.complete("ok")
  }
}

object SkillHandler {
  def props(rc: RequestContext): Props = Props(classOf[SkillHandler], rc)

  sealed trait SkillHandlerProtocol
  case class HandleSkill(jsonNode: JsonNode) extends SkillHandlerProtocol
}
