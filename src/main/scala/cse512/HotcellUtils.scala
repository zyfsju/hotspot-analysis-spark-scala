package cse512

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

object HotcellUtils {
  val coordinateStep = 0.01

  def CalculateCoordinate(inputString: String, coordinateOffset: Int): Int =
  {
    // Configuration variable:
    // Coordinate step is the size of each cell on x and y
    var result = 0
    coordinateOffset match
    {
      case 0 => result = Math.floor((inputString.split(",")(0).replace("(","").toDouble/coordinateStep)).toInt
      case 1 => result = Math.floor(inputString.split(",")(1).replace(")","").toDouble/coordinateStep).toInt
      // We only consider the data from 2009 to 2012 inclusively, 4 years in total. Week 0 Day 0 is 2009-01-01
      case 2 => {
        val timestamp = HotcellUtils.timestampParser(inputString)
        result = HotcellUtils.dayOfMonth(timestamp) // Assume every month has 31 days
      }
    }
    return result
  }

  def timestampParser (timestampString: String): Timestamp =
  {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val parsedDate = dateFormat.parse(timestampString)
    val timeStamp = new Timestamp(parsedDate.getTime)
    return timeStamp
  }

  def dayOfYear (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_YEAR)
  }

  def dayOfMonth (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_MONTH)
  }

  def GetNeighbors(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Boolean =
  {
    var withinRange = x1 - 1 <= x2 && x2 <= x1 + 1 && y1 - 1 <= y2 && y2 <= y1 + 1 && z1 - 1 <= z2 && z2 <= z1 + 1
    var isItself = x1 == x2 && y1 == y2 && z1 == z2
    if (withinRange && !isItself) {
        return true
      }
      else {
        return false
      }
  }

  def GetNeighborCount(x1: Int, y1: Int, z1: Int, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): Int =
  {
    var xOnBorders = x1 == maxX || x1 == minX
    var yOnBorders = y1 == maxY || y1 == minY
    var zOnBorders = z1 == maxZ || z1 == minZ
    var condition = List(xOnBorders, yOnBorders, zOnBorders)
    var onBorders = condition.filter(_ == true).length
    if (onBorders == 0){
      return 27
    }
    if (onBorders == 1){
      return 18
    }
    if (onBorders == 2){
      return 12
    }
    if (onBorders == 3){
      return 8
    }
    return 1
  }
}
