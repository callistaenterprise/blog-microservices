package se.callista.microservices.core.product.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by magnus on 04/03/15.
 */
@Document
public class ProductEntity {

    @Id
    private String id;

    @Version
    private int version;

    private int productId;
    private String name;
    private int weight;

    public ProductEntity() {
    }

    public ProductEntity(int productId, String name, int weight) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format(
            "ProductEntity['%s'.%d: %d, '%s', %d]",
            id, version, productId, name, weight);
    }

}
