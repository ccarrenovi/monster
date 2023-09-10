package io.redeasy.shopcar.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.redeasy.shopcar.entity.Car;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarRepository implements PanacheRepository<Car> {

}
