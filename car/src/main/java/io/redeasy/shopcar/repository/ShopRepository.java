package io.redeasy.shopcar.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.redeasy.shopcar.entity.Shop;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShopRepository implements PanacheRepository<Shop>{

}
