# How to run demo

* docker-compose up
* run the application
* docker container ls -> get kafka container ID
* docker exec -it kafka /bin/bash
* /usr/bin/kafka-console-producer --bootstrap-server localhost:9092 --topic my-topic
* open index.html page