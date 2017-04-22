package se.callista.microservices.composite.product.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.callista.microservices.composite.product.model.ProductAggregated;
import se.callista.microservices.model.Product;
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

    private ProductCompositeService service = new ProductCompositeService();

    private ProductCompositeIntegration mock = Mockito.mock(ProductCompositeIntegration.class);

    private ServiceUtils util = new ServiceUtils();

    @Before
    public void beforeTest() {

        when(mock.getProduct(any(Integer.class))).
            thenAnswer(invocation -> util.createOkResponse(new Product(getProductId(invocation), NAME, WEIGHT, "")));

        when(mock.getRecommendations(any(Integer.class))).
            thenReturn(util.createOkResponse(new ArrayList<>()));

        when(mock.getReviews(any(Integer.class))).
            thenReturn(util.createOkResponse(new ArrayList<>()));

        service.integration = mock;
        service.util = util;
    }

    @Test
    public void testSync() {
        ResponseEntity<ProductAggregated> response = service.getProduct(ID);

        assertResponse(response);
    }

    @Test
    public void testAsync() {
        ResponseEntity<ProductAggregated> response = service.getProductAsync(ID);

        assertResponse(response);
    }

    private int getProductId(InvocationOnMock invocation) {
        return (Integer)invocation.getArguments()[0];
    }

    private void assertResponse(ResponseEntity<ProductAggregated> response) {
        assertNotNull(response);

        HttpStatus code = response.getStatusCode();
        ProductAggregated body = response.getBody();

        assertEquals(OK,     code);
        assertEquals(ID,     body.getProductId());
        assertEquals(NAME,   body.getName());
        assertEquals(WEIGHT, body.getWeight());
        assertEquals(0,      body.getRecommendations().size());
        assertEquals(0,      body.getReviews().size());
    }

}
