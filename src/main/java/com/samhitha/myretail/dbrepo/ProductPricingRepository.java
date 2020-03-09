package com.samhitha.myretail.dbrepo;


import com.samhitha.myretail.dto.ProductPricing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductPricingRepository extends MongoRepository<ProductPricing, String>{
    @Query("{ '_id' : ?0 }")
    ProductPricing findProductById(String _id);
}
