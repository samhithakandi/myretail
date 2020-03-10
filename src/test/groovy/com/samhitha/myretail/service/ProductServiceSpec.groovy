package com.samhitha.myretail.service

import com.mongodb.MongoException
import com.samhitha.myretail.dbrepo.ProductPricingRepository
import com.samhitha.myretail.domain.Product
import com.samhitha.myretail.dto.ProductPricing
import com.samhitha.myretail.external.ExternalAPI
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Function

class ProductServiceSpec extends Specification {

    ExternalAPI externalAPI = Mock(ExternalAPI)
    ProductPricingRepository repository = Mock()
    CompletableFuture completableFutureMock = Mock(CompletableFuture)
    def apiURL = "http://%s"
    ProductService productService = new ProductService(externalAPI: externalAPI,repository: repository, apiURL: apiURL)

    def "getProduct: valid productId"() {
        setup:
        def productPricing = new ProductPricing("13860427", 30.5, "USD")
        def product = new Product(13860427, "Conan the Barbarian (dvd_video)", 30.5, "USD")
        def httpResponse = Mock(HttpResponse)
        def future = CompletableFuture.completedFuture(httpResponse)
        def responseString = '{"product":{"item":{"tcin":"13860427","product_description":{"title":"Conan the Barbarian (dvd_video)"}}}}'

        when:
        def productResponse = productService.getProduct(13860427)

        then:
        1 * externalAPI.getAsyncResponse(_) >> future
        1 * httpResponse.body() >> responseString
        1 * repository.findProductById('13860427') >> productPricing

        expect:
        productResponse.id == product.id
        productResponse.name == product.name
        productResponse.currentPrice.value == product.currentPrice.value
        productResponse.currentPrice.currencyCode == product.currentPrice.currencyCode
    }

    def "getProduct: if the details are present at rest API but pricing information is not present at mongo DB"() {
        setup:
        def httpResponse = Mock(HttpResponse)
        def future = CompletableFuture.completedFuture(httpResponse)
        def responseString = '{"product":{"item":{"tcin":"13860427","product_description":{"title":"Conan the Barbarian (dvd_video)"}}}}'


        when:
        def productResponse = productService.getProduct(13860427)

        then:
        1 * externalAPI.getAsyncResponse(_) >> future
        1 * httpResponse.body() >> responseString
        1 * repository.findProductById('13860427') >> { throw new MongoException("Mongo db is down") }
        notThrown(MongoException)

        expect:
        productResponse.id == 13860427
        productResponse.name == "Conan the Barbarian (dvd_video)"
        productResponse.currentPrice.value == null
        productResponse.currentPrice.currencyCode == null
    }

    def "getProduct: if the product details are not present at rest API but pricing information is present at mongo DB"() {
        setup:
        System.setProperty("api.url", 'https://redsky.target.com/v2/pdp/tcin/%s?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics')
        def productPricing = new ProductPricing("13860427", 30.5, "USD")
        def product = new Product(13860427, "Conan the Barbarian (dvd_video)", 30.5, "USD")
        completableFutureMock.exceptionally(new Function<Throwable, String>() {
            @Override
            String apply(Throwable throwable) {
                throw new CompletionException(throwable)
            }
        })

        when:
        def productResponse = productService.getProduct(13860427)

        then:
        1 * externalAPI.getAsyncResponse(_) >> completableFutureMock
        1 * repository.findProductById('13860427') >> productPricing
        notThrown(HttpClientErrorException)

        expect:
        productResponse.id == product.id
        productResponse.name == null
        productResponse.currentPrice.value == product.currentPrice.value
        productResponse.currentPrice.currencyCode == product.currentPrice.currencyCode
    }

    def "getProduct: if the product details are not present at rest API and pricing information is not present at mongo DB"() {
        setup:
        System.setProperty("api.url", 'https://redsky.target.com/v2/pdp/tcin/%s?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics')
        def productPricing = new ProductPricing("13860427", 30.5, "USD")
        def product = new Product(13860427, "Conan the Barbarian (dvd_video)", 30.5, "USD")
        completableFutureMock.exceptionally(new Function<Throwable, String>() {
            @Override
            String apply(Throwable throwable) {
                throw new CompletionException(throwable)
            }
        })

        when:
        def productResponse = productService.getProduct(13860427)

        then:
        1 * externalAPI.getAsyncResponse(_) >> completableFutureMock
        1 * repository.findProductById('13860427') >> {throw new MongoException("Mongo DB is down")}
        def e =thrown(HttpClientErrorException)
        e.statusCode.'4xxClientError'

    }

}
