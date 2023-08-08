package com.redhat.inps.kgts.demo;

import com.redhat.inps.kgts.demo.models.Order;
import com.redhat.inps.kgts.demo.models.OrderWithProduct;
import com.redhat.inps.kgts.demo.models.Product;
import io.debezium.serde.DebeziumSerdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class TopologyProducer {


    @Autowired
    void testPipeline(StreamsBuilder streamsBuilder){

        final var productKeySerde = DebeziumSerdes.payloadJson(Long.class);
        productKeySerde.configure(Collections.emptyMap(), true);

        final var productSerde = DebeziumSerdes.payloadJson(Product.class);
        productSerde.configure(Collections.singletonMap("from.field", "after"), false);

        // PRODUCT Stream to Table
//        streamsBuilder.stream("server1.testDB.dbo.products", Consumed.with(productKeySerde, productSerde))
//                .to("productsCatalog", Produced.with(productKeySerde, productSerde));

        // PRODUCT GlobalKTable
        GlobalKTable<Long, Product> products =
                streamsBuilder.globalTable("server1.testDB.dbo.products", Consumed.with(productKeySerde, productSerde),
                        Materialized.<Long, Product, KeyValueStore<Bytes, byte[]>>as("product-store")
                                .withKeySerde(productKeySerde)
                                .withValueSerde(productSerde)
                                .withLoggingEnabled(Collections.emptyMap())
                );


        final var orderKeySerde = DebeziumSerdes.payloadJson(Long.class);
        orderKeySerde.configure(Collections.emptyMap(), true);

        final var orderSerde = DebeziumSerdes.payloadJson(Order.class);
        orderSerde.configure(Collections.singletonMap("from.field", "after"), false);

        KStream<Long, Order> orders =
                streamsBuilder.stream("server1.testDB.dbo.orders", Consumed.with(orderKeySerde, orderSerde));


        orders.leftJoin(products, (aLong, order) -> Long.valueOf(order.product_id), OrderWithProduct::new)
                .to("orders-with-products", Produced.with(orderKeySerde, DebeziumSerdes.payloadJson(OrderWithProduct.class)));
    }
}
