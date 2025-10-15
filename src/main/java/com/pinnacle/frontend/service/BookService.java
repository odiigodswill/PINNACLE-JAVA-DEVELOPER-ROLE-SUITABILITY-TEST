package com.pinnacle.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinnacle.frontend.model.Book;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class BookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:8080/api/books";

    // ✅ Add a new book
    public Book save(Book book) {
        try {
            ResponseEntity<Book> response = restTemplate.postForEntity(BASE_URL, book, Book.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save book: " + e.getMessage(), e);
        }
    }

    // ✅ Update an existing book
    public Book update(Book book) {
        try {
            String url = BASE_URL + "/" + book.getId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Book> entity = new HttpEntity<>(book, headers);
            restTemplate.put(url, entity);

            // GET updated book back for confirmation
            return restTemplate.getForObject(url, Book.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update book: " + e.getMessage(), e);
        }
    }

    // ✅ Delete book by ID
    public void delete(Long id) {
        try {
            String url = BASE_URL + "/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete book: " + e.getMessage(), e);
        }
    }

    // ✅ Fetch all books (no pagination version)
    public List<Book> findAll() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL, List.class);
            return objectMapper.convertValue(response.getBody(), new TypeReference<List<Book>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching books: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ✅ Fetch books with pagination or search
    public Page<Book> query(int pageIndex, int pageSize, String search) {
        try {
            String url;
            if (search != null && !search.isBlank()) {
                url = BASE_URL + "/search?keyword=" + search;
                List<Book> list = restTemplate.getForObject(url, List.class);
                List<Book> books = objectMapper.convertValue(list, new TypeReference<List<Book>>() {});
                return new Page<>(books, pageIndex, pageSize, books.size());
            } else {
                url = BASE_URL + "?page=" + pageIndex + "&size=" + pageSize;
                String jsonResponse = restTemplate.getForObject(url, String.class);

                Map<String, Object> jsonNode = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
                List<Book> books = objectMapper.convertValue(jsonNode.get("content"), new TypeReference<List<Book>>() {});

                int totalElements = (int) jsonNode.getOrDefault("totalElements", books.size());
                int totalPages = (int) jsonNode.getOrDefault("totalPages", 1);

                return new Page<>(books, pageIndex, pageSize, totalElements, totalPages);
            }
        } catch (Exception e) {
            System.err.println("Error fetching books: " + e.getMessage());
            return new Page<>(Collections.emptyList(), pageIndex, pageSize, 0, 1);
        }
    }

    public void reload() {
        // For future caching or refresh logic if needed
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
