package com.pinnacle.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinnacle.frontend.model.Book;
import lombok.Data;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;

@Data
public class BookService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "http://localhost:8080/api/books";

    public BookService() {
        restClient = RestClient.create();
    }

    // ✅ Add a new book
    public Book save(Book book) {
        try {
            return restClient.post()
                    .uri(BASE_URL)
                    .body(book)
                    .retrieve()
                    .body(Book.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Failed to save book: " + e.getResponseBodyAsString(), e);
        }
    }

    // ✅ Update an existing book
    public Book update(Book book) {
        try {
            return restClient.put()
                    .uri(BASE_URL + "/" + book.getId())
                    .body(book)
                    .retrieve()
                    .body(Book.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Failed to update book: " + e.getResponseBodyAsString(), e);
        }
    }

    // ✅ Delete book by ID
    public void delete(Long id) {
        try {
            restClient.delete()
                    .uri(BASE_URL + "/" + id)
                    .retrieve();
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Failed to delete book: " + e.getResponseBodyAsString(), e);
        }
    }

    // ✅ Fetch all books (first page by default)
    public Page<Book> query(int pageIndex, int pageSize, String search) {
        try {
            String url = BASE_URL + "?page=" + pageIndex + "&size=" + pageSize;
            if (search != null && !search.isBlank()) {
                url = BASE_URL + "/search?keyword=" + search;
                List<Book> list = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(List.class);
                return new Page<>(objectMapper.convertValue(list, new TypeReference<List<Book>>(){}),
                        pageIndex, pageSize, list.size());
            } else {
                var pageResponse = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);

                // Parse JSON manually because Spring Boot pagination wraps results in content[], etc.
                var jsonNode = objectMapper.readTree(pageResponse);
                var listNode = jsonNode.get("content");
                var totalElements = jsonNode.get("totalElements").asInt();
                var totalPages = jsonNode.get("totalPages").asInt();

                List<Book> books = objectMapper.convertValue(listNode, new TypeReference<List<Book>>() {});
                return new Page<>(books, pageIndex, pageSize, totalElements, totalPages);
            }
        } catch (Exception e) {
            System.err.println("Error fetching books: " + e.getMessage());
            return new Page<>(Collections.emptyList(), pageIndex, pageSize, 0, 1);
        }
    }

    public void reload() {
        // No caching implemented; so reload just does nothing
    }

    // ✅ Pagination helper class
    @Data
    public static class Page<T> {
        private final List<T> items;
        private final int pageIndex;
        private final int pageSize;
        private final int totalCount;
        private final int totalPages;

        public Page(List<T> items, int pageIndex, int pageSize, int totalCount) {
            this(items, pageIndex, pageSize, totalCount,
                    (int) Math.ceil((double) totalCount / pageSize));
        }

        public Page(List<T> items, int pageIndex, int pageSize, int totalCount, int totalPages) {
            this.items = items;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.totalCount = totalCount;
            this.totalPages = totalPages;
        }
    }
}
