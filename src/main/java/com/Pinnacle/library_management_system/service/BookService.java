package com.Pinnacle.library_management_system.service;

import com.Pinnacle.library_management_system.model.Book;
import com.Pinnacle.library_management_system.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // Add new book
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // Get all books with pagination
    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return bookRepository.findAll(pageable);
    }

    // Get book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Update book
    public Optional<Book> updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setIsbn(updatedBook.getIsbn());
            book.setPublishedDate(updatedBook.getPublishedDate());
            return bookRepository.save(book);
        });
    }

    // Delete book
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Search books by title or author
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }
}
