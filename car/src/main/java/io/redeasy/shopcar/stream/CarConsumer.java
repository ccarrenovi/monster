package io.redeasy.shopcar.stream;

import io.redeasy.shopcar.entity.Car;
import io.smallrye.reactive.messaging.kafka.Record;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class CarConsumer {
	
    private final Logger logger = LoggerFactory.getLogger(Car.class);

    @Incoming("car-in")
    public void receive(Record<Long, Car> record) {
        Car car = record.value();
        String data = " Car: {key:"+record.key()+",price:"+car.getPrice()+",model:"+car.getModel()+"}";
        logger.info(data);
 }
}