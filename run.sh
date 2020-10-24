#! /bin/bash

docker-compose up -d grafana
docker-compose up -d prometheus
docker-compose up -d postgres
docker-compose up -d redis
docker-compose up -d rabbitmq

# detect


# start consumer