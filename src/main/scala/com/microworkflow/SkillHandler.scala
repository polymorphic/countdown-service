package com.microworkflow

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.microworkflow.SkillHandler.HandleSkill
import spray.http.MediaTypes
import spray.httpx.marshalling.Marshaller
import spray.routing.RequestContext

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by dam on 11/6/16.
  */
class SkillHandler(rc: RequestContext, generator: ActorRef) extends Actor with ActorLogging {
  /*
  Intent schema:
  {
  "intents": [
    {
      "intent": "GetNewFactIntent"
    },
    {
      "intent": "AMAZON.HelpIntent"
    },
    {
      "intent": "AMAZON.StopIntent"
    },
    {
      "intent": "AMAZON.CancelIntent"
    }
  ]
  }

  Sample utterances:
  GetNewFactIntent a fact
  GetNewFactIntent a kitty fact
  GetNewFactIntent tell me a fact
  GetNewFactIntent tell me a kitty fact
  GetNewFactIntent give me a fact
  GetNewFactIntent give me a kitty fact
  GetNewFactIntent tell me trivia
  GetNewFactIntent tell me a kitty trivia
  GetNewFactIntent give me trivia
  GetNewFactIntent give me a kitty trivia
  GetNewFactIntent give me some information
  GetNewFactIntent give me some kitty information
  GetNewFactIntent tell me something
  GetNewFactIntent give me something
   */
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  implicit val objectNodeMarshaller =
    Marshaller.delegate[ObjectNode, String](MediaTypes.`application/json`)(objectNode ⇒
      mapper.writeValueAsString(objectNode)
    )

  override def receive: Receive = {
    case HandleSkill(jsonNode) ⇒
      val message =
        Try { jsonNode.get("request").get("type").asText() } match {
          case scala.util.Success(requestType) ⇒
            def getNewFact(): String = {
              implicit val timeout: akka.util.Timeout = 1.second
              val q = (generator ? 'getFact).mapTo[String]
              Await.result(q, timeout.duration)
            }

            if (requestType == "IntentRequest") {
              Try { jsonNode.get("request").get("intent").get("name").asText()}
                  .map(n ⇒ if (n == "GetNewFactIntent") getNewFact() else "I don't understand your intent")
                    .getOrElse("I don't understand your request")
            } else {
              "I'm not quite ready to answer your question yet, sorry!"
            }
          case scala.util.Failure(_) ⇒ "I don't understand your question"
      }
      rc.complete(SkillHandler.buildResponse(message))
  }
}

object SkillHandler {
  def props(rc: RequestContext, factGenerator: ActorRef): Props = Props(classOf[SkillHandler], rc, factGenerator)

  sealed trait SkillHandlerProtocol
  case class HandleSkill(jsonNode: JsonNode) extends SkillHandlerProtocol

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  /*
  https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interface-reference#response-format
   */
  def buildResponse(text: String): ObjectNode = {
    val r = mapper.createObjectNode()
    r.put("version", "1.0")
    val responseElement = mapper.createObjectNode()
    responseElement.put("shouldEndSession", true)
    val outputSpeechElement = mapper.createObjectNode()
    outputSpeechElement.put("type", "PlainText")
    outputSpeechElement.put("text", text)
    responseElement.set("outputSpeech", outputSpeechElement)
    r.set("response", responseElement)
    r
  }
}
