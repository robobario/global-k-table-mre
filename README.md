# Kafka Stream Global K Table 

This is a minimum reproducible example to test the behaviour of GlobalKTable, when the Kstream application start with an already populated topic.

The environment is based on the [Debezium tutorial for SQL Server](https://github.com/debezium/debezium-examples/blob/main/tutorial/README.md#using-sql-server)

## How to reproduce

Steps:

1. Set up the environment 
 
```
docker-compose up
```
2. Prepare database
```
cat sql-server-init.sql | docker-compose exec -T sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P $SA_PASSWORD'
```

3. Register debezium connector
```
curl -i -X POST \
 -H "Accept:application/json" \
 -H "Content-Type:application/json" \
 -d @register-sqlserver.json \
  http://localhost:8083/connectors/ 
```

4. Verify topics exist: 

```
docker-compose exec -T kafka /kafka/bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --list 
```

Expected output should be

```
__consumer_offsets
my_connect_configs
my_connect_offsets
my_connect_statuses
schema-changes.inventory
server1
server1.testDB.dbo.customers
server1.testDB.dbo.orders
server1.testDB.dbo.products
server1.testDB.dbo.products_on_hand
```

5. Launch the Streams application

```
mvn spring-boot:run
```


**Expected Behaviour**

Product store is populated with all product records already present on kafka.

Invoking the materialized view should return the product

```
http :8080/products/102
```  

Consuming the `orders-with-products` topic should have the product (resulting from leftJoin) populated.

```
docker-compose exec -T kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic orders-with-products --from-beginning
```

**Actual Behaviour**

- Materialized view is empty
- Products inside the `orders-with-products` is null


## Additional info

Inserting a new product and then an order that refers such product results in a correct behaviour. 


Steps: 

1. Insert a new product
```
cat <<-EOF | docker-compose exec -T sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P $SA_PASSWORD'
USE testDB;
INSERT INTO products(name,description,weight)
  VALUES ('jacket','water resistent black wind breaker',0.1);
GO
EOF
```
2. Verify that the new product exists in the store

```
http :8080/products/110
```

Output:

```
HTTP/1.1 200 

{
    "description": "water resistent black wind breaker",
    "id": 110,
    "name": "jacket",
    "weight": 0.1
}
```


3. Insert a new Order for the product
```
cat <<-EOF | docker-compose exec -T sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P $SA_PASSWORD'
USE testDB;
INSERT INTO orders(order_date,purchaser,quantity,product_id)
    VALUES ('16-JAN-2016', 1001, 1, 110)
GO
EOF
```

4. Verify that last record on `orders-with-products` is correct.

```
docker-compose exec -T kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic orders-with-products --from-beginning
```

Output: 

```
{"order":{"id":10005,"order_date":"16816","purchaser":1001,"quantity":1,"product_id":110},"product":{"id":110,"name":"jacket","description":"water resistent black wind breaker","weight":0.1},"customer":null}

```



## Workaround

Add these lines in the TopologyProducer class.

```

        streamsBuilder.stream("server1.testDB.dbo.products", Consumed.with(productKeySerde, productSerde))
                .to("productsCatalog", Produced.with(productKeySerde, productSerde));
```

Change the topic for the GlobalKTable 

```
        // PRODUCT GlobalKTable
        GlobalKTable<Long, Product> products =
                streamsBuilder.globalTable("productsCatalog", ...
```