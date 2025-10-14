package com.pinnacle.frontend.service;

import com.pinnacle.frontend.model.Book;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Data
public class BookService {

    private final Map<Long, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public BookService() {
        // seed with sample data
        for (int i = 1; i <= 53; i++) {
            save(new Book(null, "Book title " + i, "Author " + (i % 7 + 1), "ISBN" + i,
                    LocalDate.now().minusDays(i * 30L)));
        }
    }

    public synchronized Book save(Book book) {
        if (book.getId() == null) {
            long id = idGen.getAndIncrement();
            book.setId(id);
        }
        store.put(book.getId(), book);
        return book;
    }

    public synchronized boolean delete(Long id) {
        return store.remove(id) != null;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Query page of data. Page index is 0-based.
     */
    public Page<Book> query(int pageIndex, int pageSize, String search) {
        List<Book> list = new ArrayList<>(store.values());
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            list = list.stream()
                    .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(s))
                            || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(s)))
                    .collect(Collectors.toList());
        }
        list.sort(Comparator.comparing(Book::getId));
        int total = list.size();
        int from = pageIndex * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Book> pageItems = new ArrayList<>();
        if (from < total) pageItems = list.subList(from, to);
        return new Page<>(pageItems, pageIndex, pageSize, total);
    }

    public void reload() {
        // For a simulated backend, reload might re-seed or do nothing.
        // We will do nothing here — but method is present to satisfy the UI reload action.
    }

    public static class Page<T> {
        private final List<T> items;
        private final int pageIndex;
        private final int pageSize;
        private final int totalCount;

        public Page(List<T> items, int pageIndex, int pageSize, int totalCount) {
            this.items = items;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.totalCount = totalCount;
        }

        public List<T> getItems() { return items; }
        public int getPageIndex() { return pageIndex; }
        public int getPageSize() { return pageSize; }
        public int getTotalCount() { return totalCount; }

        public int getTotalPages() {
            return (int) Math.ceil((double) totalCount / pageSize);
        }
    }
}
