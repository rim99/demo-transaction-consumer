#! /bin/bash

docker-compose up -d grafana
docker-compose up -d prometheus
docker-compose up -d postgres
docker-compose up -d redis
docker-compose up -d rabbitmq

echo "Third party services have started"
echo "visit http://localhost:3000 for grafana: [user]=admin, [password]=admin"
echo "visit http://localhost:9090 for prometheus"
echo "visit http://localhost:15672 for rabbitmq: [user]=user, [password]=passwd"

# build
docker-compose build consumer-hotspot
docker-compose build consumer-openj9

# start consumer
docker-compose up -d consumer-hotspot
docker-compose up -d consumer-openj9