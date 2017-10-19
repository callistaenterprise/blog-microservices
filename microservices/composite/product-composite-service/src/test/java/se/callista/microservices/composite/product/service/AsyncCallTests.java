package se.callista.microservices.composite.product.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.microservices.composite.product.model.ProductAggregated;
import se.callista.microservices.model.Product;
import se.callista.microservices.model.Review;
import se.callista.microservices.util.ServiceUtils;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

/**
 * Created by magnus on 15/07/16.
 */
public class AsyncCallTests {

    private static final int    ID     = 123;
    private static final String NAME   = "name";
    private static final int    WEIGHT = 456;

    private ProductCompositeService service;

    private ProductCompositeIntegration mock = Mockito.mock(ProductCompositeIntegration.class);

//    private ServiceUtils util = new ServiceUtils(null, null);
    private ServiceUtils util = new ServiceUtils(null);

    @Before
    public void beforeTest() {

        // ------------ //
        // ASYNCH MOCKS //
        // ------------ //
        when(mock.getProductAsync(any(Integer.class))).
            thenReturn(Mono.just(new Product(ID, NAME, WEIGHT, "")));

        when(mock.getRecommendationsAsync(any(Integer.class))).
            thenReturn(Flux.empty());

        when(mock.getReviewsAsync(any(Integer.class))).
            thenReturn(Flux.empty());

        // ----------- //
        // SYNCH MOCKS //
        // ----------- //
        when(mock.getProduct(any(Integer.class))).
            thenAnswer(invocation -> {
                Product product = new Product(getProductId(invocation), NAME, WEIGHT, "");
                return util.createOkResponse(product);
            });

        when(mock.getRecommendations(any(Integer.class))).
            thenReturn(util.createOkResponse(new ArrayList<>()));

        when(mock.getReviews(any(Integer.class))).
            thenReturn(util.createOkResponse(new ArrayList<>()));

        service = new ProductCompositeService(mock, util);

    }

    @Test
    public void testSync() {
        ResponseEntity<ProductAggregated> response = service.getProductAggregatedSync(ID);

        assertResponse(response);
    }

    @Test
    public void testAsync() {
        ProductAggregated response = service.getProductAggregatedAsync(ID).block();

        assertResponseBody(response);
    }

    private int getProductId(InvocationOnMock invocation) {
        return (Integer)invocation.getArguments()[0];
    }

    private void assertResponse(ResponseEntity<ProductAggregated> response) {
        assertNotNull(response);
        assertEquals(OK, response.getStatusCode());
        assertResponseBody(response.getBody());
    }

    private void assertResponseBody(ProductAggregated body) {

        assertEquals(ID,     body.getProductId());
        assertEquals(NAME,   body.getName());
        assertEquals(WEIGHT, body.getWeight());
        assertEquals(0,      body.getRecommendations().size());
        assertEquals(0,      body.getReviews().size());
    }
}
