package io.redeasy.shopcar;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;


@QuarkusTest
@Tag("integration")
@TestMethodOrder(OrderAnnotation.class)
public class CarResourceTest {

    @Test
    @Order(1)
    void getAll() {
    	given()
    	 .when()
    	 .get("/cars")
    	 .then()
    	 .statusCode(200)
    	 .body("year", hasItem("2007")); 	 
    }
	
    @Test
    @Order(1)
    void getById() {
    	given()
    	 .when()
    	 .get("/cars/1")
    	 .then()
    	 .statusCode(200)
    	 .body("model", is("BMW 525"));  	 
    }

}