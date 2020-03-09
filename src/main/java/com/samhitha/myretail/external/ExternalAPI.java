package com.samhitha.myretail.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class ExternalAPI {

    @Autowired
    HttpClient httpClient;

    public CompletableFuture<HttpResponse<String>> getAsyncResponse(String uri) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

}
