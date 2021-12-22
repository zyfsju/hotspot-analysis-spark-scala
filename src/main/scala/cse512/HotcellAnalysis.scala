package cse512

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

  def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
  {
    // Load the original data from a data source
    var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter",";").option("header","false").load(pointPath);
    pickupInfo.createOrReplaceTempView("nyctaxitrips")
    pickupInfo.show()

    // Assign cell coordinates based on pickup points
    spark.udf.register("CalculateX",(pickupPoint: String)=>((
      HotcellUtils.CalculateCoordinate(pickupPoint, 0)
      )))
    spark.udf.register("CalculateY",(pickupPoint: String)=>((
      HotcellUtils.CalculateCoordinate(pickupPoint, 1)
      )))
    spark.udf.register("CalculateZ",(pickupTime: String)=>((
      HotcellUtils.CalculateCoordinate(pickupTime, 2)
      )))
    pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
    var newCoordinateName = Seq("x", "y", "z")
    pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
    pickupInfo.show()

    // Define the min and max of x, y, z
    val minX = -74.50/HotcellUtils.coordinateStep
    val maxX = -73.70/HotcellUtils.coordinateStep
    val minY = 40.50/HotcellUtils.coordinateStep
    val maxY = 40.90/HotcellUtils.coordinateStep
    val minZ = 1
    val maxZ = 31
    val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)

    pickupInfo.createOrReplaceTempView("nyctaxitrips")
    var pickupCount = spark.sql("select x, y, z, count(*) as count from nyctaxitrips group by x, y, z")
    pickupCount.createOrReplaceTempView("tripcount")
    pickupCount.show()

    var avgCount = spark.sql(s"select sum(count)/$numCells as count from tripcount").first()(0)

    var s = spark.sql(s"select sqrt(sum(count*count)/$numCells-$avgCount*$avgCount) as s from tripcount").first()(0)
    spark.sql(s"select sqrt(sum(count*count)/$numCells-$avgCount*$avgCount) as s from tripcount").show()

    spark.udf.register("GetNeighborCount",(x1: Int, y1: Int, z1: Int, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int)=>(HotcellUtils.GetNeighborCount(x1, y1, z1, minX, minY, minZ, maxX, maxY, maxZ)))

    spark.udf.register("GetNeighbors",(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int)=>(HotcellUtils.GetNeighbors(x1, y1, z1, x2, y2, z2)))

    val joinDf = spark.sql(s"select t1.x, t1.y, t1.z, sum(t2.count) as neighborSum, GetNeighborCount(t1.x, t1.y, t1.z, $minX, $minY, $minZ, $maxX, $maxY, $maxZ) as neighborCount from tripcount t1, tripcount t2 where GetNeighbors(t1.x, t1.y, t1.z, t2.x, t2.y, t2.z) group by t1.x, t1.y, t1.z")
    joinDf.createOrReplaceTempView("joinResult")
    joinDf.show()
    val GScore = spark.sql(s"select x, y, z, (neighborSum-$avgCount*neighborCount)/($s*sqrt(pow($numCells*neighborCount - neighborCount*neighborCount, 2)/($numCells-1))) as GScore from joinResult order by GScore desc limit 50")

    // GScore.show()
    GScore.createOrReplaceTempView("GScoreResult")
    val top50GScore = spark.sql(s"select x, y, z from GScoreResult")

    return top50GScore
  }
}
