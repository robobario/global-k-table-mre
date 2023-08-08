package com.redhat.inps.kgts.demo;

import com.redhat.inps.kgts.demo.models.Product;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductResource {


    @Autowired
    StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/products/{productId}")
    public Product getWordCount(@PathVariable Long productId) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<Long, Product> products = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType("product-store", QueryableStoreTypes.keyValueStore())
        );
        return products.get(productId);
    }


}
