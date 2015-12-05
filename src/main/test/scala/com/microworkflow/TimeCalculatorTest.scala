package com.microworkflow

import org.scalatest.FunSuite

import scala.util.{Failure, Success}

/**
  * Created by dam on 12/5/15.
  */
class TimeCalculatorTest extends FunSuite {

  test("parse date") {
    val dateString = "2015/11/10 23:59"
    TimeCalculator.parseTime(dateString) match {
      case Success(dt) ⇒
        assert(2015 === dt.getYear)
        assert(11 === dt.getMonthOfYear)
        assert(10 === dt.getDayOfMonth)
        assert(23 === dt.getHourOfDay)
        assert(59 === dt.getMinuteOfHour)
      case Failure(t) ⇒ fail("parsing failed", t)
    }
  }

}
