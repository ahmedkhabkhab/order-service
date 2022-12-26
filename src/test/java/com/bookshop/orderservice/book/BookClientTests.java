package com.bookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

public class BookClientTests {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        var webClient = WebClient.builder().baseUrl(mockWebServer.url("/").uri().toString()).build();
        this.bookClient = new BookClient(webClient);
    }

    @Test
    public void whenBookExistsThenReturnBook() {
        var isbn = "123";

        // Define a mock response returned by the mock server
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "isbn": %s,
                          "title": "Title",
                          "author": "Author",
                          "price": 100.0,
                          "publisher": "Publisher"
                        }
                        """.formatted(isbn));

        // Add Mock response to the queue processed by the Mock server
        mockWebServer.enqueue(mockResponse);

        // Operation
        Mono<Book> book = bookClient.getBookByIsbn(isbn);

        // Verify that reactive stream completes successfully
        StepVerifier.create(book).expectNextMatches(b -> isbn.equals(b.isbn())).verifyComplete();
    }

    @AfterEach
    public void clean() throws IOException {
        this.mockWebServer.shutdown();
    }

}
