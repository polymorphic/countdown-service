package com.microworkflow

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.ConfigFactory

import scala.util.Random

/**
  * Created by dam on 11/6/16.
  */

class FactGenerator extends Actor with ActorLogging {
  val r = new Random(System.currentTimeMillis())
  import scala.collection.JavaConverters._

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    val factsConfig = ConfigFactory.parseResources("facts.json")
    val key = "cat-facts"
    val facts = factsConfig.getStringList(key).asScala.toSeq
    log.debug(s"loaded ${facts.size} entires from $key")
    context.become(receiveWithFacts(facts))
  }

  override def receive: Receive = {
    case _ ⇒ /* nop */
  }

  def receiveWithFacts(allFacts: Seq[String]): Receive = {
    case 'getFact ⇒
      sender() ! allFacts(r.nextInt(allFacts.size - 1))
  }
}
