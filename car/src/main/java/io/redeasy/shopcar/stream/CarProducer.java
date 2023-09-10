package io.redeasy.shopcar.stream;

import io.redeasy.shopcar.entity.Car;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CarProducer {

    @Inject 
    @Channel("car-out")
    Emitter<Record<String, Car>> carEmitter;

    public void sendCarToKafka(Car car) {
        carEmitter.send(Record.of(car.getBrand(), car));
    }
}
