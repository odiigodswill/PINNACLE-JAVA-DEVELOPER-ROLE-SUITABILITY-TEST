package com.pinnacle.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pinnacle.frontend.model.Book;
import lombok.Data;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.SerializationFeature;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class BookService {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/api/books";
    ObjectMapper objectMapper = new ObjectMapper();

    public BookService() {
        this.restTemplate = new RestTemplate();

        // Configure ObjectMapper to handle LocalDate and Java 8 time types

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use the configured ObjectMapper
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        this.restTemplate.getMessageConverters().add(converter);
    }

    public Book[] getAllBooks() {
        return restTemplate.getForObject(BASE_URL, Book[].class);
    }

    public Book save(Book book) {
        return restTemplate.postForObject(BASE_URL, book, Book.class);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
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
