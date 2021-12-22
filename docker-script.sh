#!/bin/bash
docker-compose build spark-scala-env
docker-compose build app-spark-scala
docker-compose up spark-master
docker-compose up app-submit-job
