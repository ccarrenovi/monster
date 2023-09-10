package io.redeasy.shopcar.stream;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.redeasy.shopcar.entity.Car;

public class CarDeserializer extends ObjectMapperDeserializer<Car> {
    
    public CarDeserializer(){
        super(Car.class);
    }

}
