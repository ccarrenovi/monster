package io.redeasy.shopcar.proxy;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.redeasy.shopcar.entity.Car;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cars")
@Produces(MediaType.APPLICATION_JSON)
//@RegisterRestClient(baseUri = "http://localhost:8080/")
//@RegisterRestClient
@RegisterRestClient(baseUri = "stork://car-service")
public interface CarProxy {

	@GET
	@Path("shop/{idshop}")
	List<Car> getCarsByShopId(@PathParam("idshop") Long idShop);
}
