package com.samhitha.myretail.controller

import com.samhitha.myretail.domain.Product
import com.samhitha.myretail.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

/**
 * {@link com.samhitha.myretail.controller.ProductController} unit test.
 */
class ProductControllerSpec extends Specification {
    def productService = Mock(ProductService)
    def productController = new ProductController(service: productService)

    def "getProductById: using valid productId"() {
        setup:
        def product = new Product(13860427, "Conan the Barbarian (dvd_video)", 30.5, "USD")

        when:
        ResponseEntity<Product> response = productController.getProductById(12345)

        then:
        1 * productService.getProduct(12345) >> product

        then:
        response.statusCode.'2xxSuccessful'
        response.body == product

    }

    def "putProduct: when try to update id"() {
        setup:
        def product = new Product(13860420, "Conan the Barbarian (dvd_video)", 30.5, "USD")

        when:
        ResponseEntity<Product> response = productController.putProduct(13860420, product)

        then:
        1 * productService.updateProduct(13860420, product) >> product

        expect:
        response.statusCode.'2xxSuccessful'
        response.body == product

    }

}