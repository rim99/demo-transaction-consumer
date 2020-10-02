
# How to check keys & values in redis

## connect via redis-cli

```
docker exec -it transaction-consumer_redis_1 redis-cli
```

## get all keys

```
keys *
```

## get key's value

```
get [KEY]
```
