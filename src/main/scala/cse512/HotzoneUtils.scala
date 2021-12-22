package cse512

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean = {
    // Split the string to get xMin, yMin, xMax, yMax for the rectangle
    var rectangle = queryRectangle.split(",").map(_.toDouble)
    // Split the string to get x, y for the point
    var point = pointString.split(",").map(_.toDouble)
    // If the point x, y falls within the rectangle, return true. Otherwise, return false.
    if (rectangle(0) <= point(0) && point(0) <= rectangle(2) && rectangle(1) <= point(1) && point(1) <= rectangle(3)) {
        return true
      }
      else {
        return false
      }
  }
}
