package com.microworkflow

import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}

import scala.util.Try

/**
  * Created by dam on 12/5/15.
  */

object TimeCalculator {
  val formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm")
  val isoFormater = ISODateTimeFormat.dateTime()

  def parseTime(s: String): Try[DateTime] = Try { formatter.parseDateTime(s) }

  def formatTime(dt: DateTime): String = isoFormater.print(dt)
}
