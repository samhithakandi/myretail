package com.samhitha.myretail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.samhitha.myretail.dbrepo.ProductPricingRepository;
import com.samhitha.myretail.domain.Product;
import com.samhitha.myretail.dto.ProductPricing;
import com.samhitha.myretail.external.ExternalAPI;
import com.samhitha.myretail.external.response.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ExternalAPI externalAPI;

    @Autowired
    ProductPricingRepository repository;

    @Value("${api.url}")
    String apiURL;


    /**
     * Calls mongo db for product pricing details and calls REST API for getting product title using productId
     * The product attribute is passed as Long but we treat it as string in mongo.
     *
     * @param productId the id or tcin of the product
     * @return The product object which includes title and pricing
     * @throws HttpClientErrorException
     * @throws HttpServerErrorException
     */
    public Product getProduct(Long productId) throws HttpClientErrorException, HttpServerErrorException {
        ProductPricing productPricing;
        String title;
        try {
            String apiURLWithProductId = String.format(apiURL, productId.toString());
            CompletableFuture<HttpResponse<String>> completableFuture = externalAPI.getAsyncResponse(apiURLWithProductId);
            productPricing = getFromMongo(productId.toString());
            ProductResponse productResponse = handleAPIResponse(completableFuture);
            if (productResponse == null && productPricing == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
            title = productResponse != null && productResponse.getProduct().getItem().getProductDescription() != null ? productResponse.getProduct().getItem().getProductDescription().getTitle() : null;
            return new Product(productId, title, productPricing == null ? null : productPricing.getPrice(), productPricing == null ? null : productPricing.getCurrency());
        } catch (HttpClientErrorException hce) {
            logger.error("External API returned exception - http status code: " + hce.getStatusCode() + " message: " + hce.getResponseBodyAsString());
            throw new HttpClientErrorException(hce.getStatusCode(), "Product Not Found");
        } catch (Exception e) {
            logger.error("Runtime exception caused by internal server error", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles api response from completable future by parsing and checking if the product is valid
     *
     * @param completableFuture Completable future to parse and get asynchronously
     * @return the response object with necessary props from the api or null
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private ProductResponse handleAPIResponse(CompletableFuture<HttpResponse<String>> completableFuture) throws InterruptedException, ExecutionException {
        try {
            String response = completableFuture.thenApply(HttpResponse::body).exceptionally(ex -> "Cannot get from API").get();
            ObjectMapper mapper = new ObjectMapper();
            ProductResponse productResponse = mapper.readValue(response, ProductResponse.class);
            if (productResponse.getProduct() != null && productResponse.getProduct().getItem() != null) {
                if (productResponse.getProduct().getItem().getTcin() == null) {
                    return null;
                } else {
                    return productResponse;
                }
            }
        } catch (IOException io) {
            logger.error("Unable to process json from api", io);
        } catch (Exception io) {
            logger.error("Unable to process json from api", io);
        }
        return null;
    }

    /**
     * Return Product Pricing object which is populated from mongo database
     *
     * @param productId Id of the product
     * @return Object with pricing information
     */
    private ProductPricing getFromMongo(String productId) {
        try {
            return repository.findProductById(productId);
        } catch (MongoException me) {
            logger.error("Cannot get product information at this time from mongo", me);
        }
        return null;
    }

    /**
     * Adds or updates pricing information of the product input in the mongo database
     *
     * @param id      Id of the product
     * @param product Product object to update pricing in the database
     * @return Updated product based on the input params
     */
    public Product updateProduct(Long id, Product product) {
        if (id.equals(product.getId())) {
            ProductPricing productPricing = mapProductPricing(id, product);
            repository.save(productPricing);
            return product;
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Product Id Mismatch.");
        }
    }

    /**
     * Maps user input for product object to Product Pricing object necessary for mongo
     *
     * @param id      Product Id passed by api
     * @param product Product Object passed by api
     * @return Return ProductPricing object to update in mongo db
     */
    private ProductPricing mapProductPricing(Long id, Product product) {
        ProductPricing productPricing = new ProductPricing();
        productPricing.setProductId(id.toString());
        if (product.getCurrentPrice() != null) {
            productPricing.setPrice(product.getCurrentPrice().getValue());
            productPricing.setCurrency(product.getCurrentPrice().getCurrencyCode());
        }
        return productPricing;
    }
}
