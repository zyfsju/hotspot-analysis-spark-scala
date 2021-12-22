sbt clean assembly
# /spark/bin/spark-submit --class cse512.Entrance --master local[*] "/app/target/scala-2.11/CSE512-Hotspot-Analysis-Template-assembly-0.1.0.jar" "/app/test/output" hotzoneanalysis "src/resources/point_hotzone.csv src/resources/zone-hotzone.csv"
# /spark/bin/spark-submit --class cse512.Entrance --master local[*] "/app/target/scala-2.11/CSE512-Hotspot-Analysis-Template-assembly-0.1.0.jar" "/app/test/output" hotcellanalysis "src/resources/yellow_tripdata_2009-01_point.csv"
/spark/bin/spark-submit --class cse512.Entrance --master local[*] "/app/target/scala-2.11/CSE512-Hotspot-Analysis-Template-assembly-0.1.0.jar" "/app/test/output" hotcellanalysis "src/resources/yellow_trip_sample_100000.csv"
