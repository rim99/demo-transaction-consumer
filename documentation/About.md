# Transaction-consumer

## Background

This is a demo for consuming transactions.

In this demo, we will consume 2 types of transactions which are created by electronic commercial payments,`purchase` and `refund`. A purchase transaction may launched via payments vendors: Alipay, Wechat pay or credit card directly. A refund transaction is valid only if its refund amount less than or equal to its `origin purchase transaction`. 

This consumer should constantly receive the transactions information from the Kafka services. The consumer will do aggregation with the received transactions. The aggreration details should include:
* the count/amount of purchase transactions per payment vendor 
* the count/amount of purchase transactions per payment type 
* the count/amount of purchase transactions per merchant
* the count/amount of valid/invalid refund transactions per payment vendor
* the count/amount of valid/invalid refund transactions per payment type 
* the count/amount of valid/invalid refund transactions per merchant

All aggregation detail should provide statistics during specific timeframe: minutely, hourly, daily.

## Requeirement

Given we have 10 million transactions one day.

* This consumer should provide necessary http API(s) for retriving aggraretion infomation for ranges up to 1,000 timeframes.
* The aggregation should be done immediately after receiving transactions and *transactionally*, which means either finish aggregation for all details or for none per transaction.


## Implementation

* Use Postgres for saving transactions in case of new aggragetion demand in the future.
* For aggregation part, try 3 steps:
  1. Use Postgres' OLAP capability
  2. Use Redis atomic update capability
  3. Use Atomic classes provided by openJDK, implement replication feature independently
* In the future, implement request logging persistence feature for http POST API which will allow clients to feed transactions directly and asychrounously.
