package com.samhitha.myretail.controller;

import com.samhitha.myretail.domain.Product;
import com.samhitha.myretail.service.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value="API for Product Details such as Title and Pricing")
public class ProductController {

    @Autowired
    ProductService service;

    @ApiOperation(value="View Product Details for the given Product Id", response = Product.class)
    @RequestMapping(method = RequestMethod.GET, value = {"/products/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the product."),
            @ApiResponse(code = 400, message = "Product Id passed as a path param is not valid."),
            @ApiResponse(code = 404, message = "The product with input id is not found.")
    })
    public ResponseEntity<Product> getProductById(
            @ApiParam(value = "Product Id from which product details will be retrieved.", required = true) @PathVariable Long id) {
        Product product = service.getProduct(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @ApiOperation(value="Updates the product pricing.", response = Product.class)
    @RequestMapping(method = RequestMethod.PUT, value = {"/products/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated and retrieved the product."),
            @ApiResponse(code = 400, message = "Product Id passed as a path param and body do not match.")
    })
    public ResponseEntity<Product> putProduct(
            @ApiParam(value = "Product Id which should be updated.", required = true) @PathVariable Long id,
            @ApiParam(value = "Product Object to be stored.", required = true) @RequestBody Product product) {

        Product updatedProduct = service.updateProduct(id, product);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}
