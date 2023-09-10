package io.redeasy.shopcar.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.jaegertracing.internal.metrics.Metric;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.redeasy.shopcar.entity.Car;
import io.redeasy.shopcar.entity.Shop;
import io.redeasy.shopcar.proxy.CarProxy;
import io.redeasy.shopcar.repository.ShopRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/shops")
public class ShopResource {

	@RestClient
	CarProxy carProxy;

	@Inject
	ShopRepository shopRepository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Timeout(5000)
	public Response getAll() {
		//qpausa(3000L);
		List<Shop> shops = shopRepository.listAll();
		return Response.ok(shops).build();
	}

	private void pausa(Long milisegundos) {
		try {
			TimeUnit.MILLISECONDS.sleep(milisegundos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Gauge(name = "gauge.shop.id", description = "Veces que se ha invocado el metodo de busqueda por {id}", unit = MetricUnits.NONE)
	@Counted(name = "count.shop.id", description = "Numero de busquedas por id")
	@Timed(name = "timed.shop.id", description = "Tiempo de respuesta de busqueda por id")
	@Retry(maxRetries = 4)
	public Response getById(@PathParam("id") Long id) {
		Shop shop = shopRepository.findById(id);
		if (shopRepository.isPersistent(shop)) {
			return Response.ok(shop).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Shop shop) {
		shopRepository.persist(shop);
		if (shopRepository.isPersistent(shop)) {
			return Response.created(URI.create("Shops" + shop.id)).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Transactional
	@Path("{id}")
	public Response deleteById(@PathParam("id") Long id) {
		boolean deleted = shopRepository.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@GET
	@Path("{id}/cars")
	@Produces(MediaType.APPLICATION_JSON)
	@Counted(name = "count.shop.cars", description = "Contador del numero de veces que se ha invocado a /{id}/cars")
	@Timed(name = "timed.shop.cars", description = "Tiempo de respuesta de la invocacion a /{id}/cars ", unit = MetricUnits.MILLISECONDS)
	@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 4000, successThreshold = 2)
	@Fallback(fallbackMethod = "fallbackGetCars")
    @Bulkhead(value = 2, waitingTaskQueue = 10)
	@RolesAllowed("user")
	public Response getCars(@PathParam("id") Long id) {
		List<Car> cars = carProxy.getCarsByShopId(id);
		System.out.println("[INFO] Consultando el {id:" + id +"} desde el metodo getCars() " );
		return Response.ok(cars).build();
	}

	private Response fallbackGetCars(Long id) {
		List<Car> cars = new ArrayList<Car>();
		System.out.println("[INFO] Consultando el {id:" + id +"} desde el fallback method fallbackGetCars() " );
		return Response.ok(cars).build();
	}

	@GET
	@Path("{id}/cars/noncache")
	@Produces(MediaType.APPLICATION_JSON)
	@CacheInvalidateAll(cacheName = "cars-cache")
	public Response getCarsWithOutCache(@PathParam("id") Long id) {
		List<Car> cars = carProxy.getCarsByShopId(id);
		return Response.ok(cars).build();
	}

}
