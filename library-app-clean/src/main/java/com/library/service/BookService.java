package com.library.service;

import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(Book book) {
        log.info("Adding new book with ISBN: {}", book.getIsbn());
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(existing -> {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists");
        });
        Book saved = bookRepository.save(book);
        log.info("Book saved with ID: {}", saved.getId());
        return saved;
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.search(keyword);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailable();
    }

    public Book updateBook(String id, Book updatedBook) {
        log.info("Updating book with ID: {}", id);
        Book existing = getBookById(id);

        // Check ISBN conflict only if changed
        if (!existing.getIsbn().equals(updatedBook.getIsbn())) {
            bookRepository.findByIsbn(updatedBook.getIsbn()).ifPresent(b -> {
                throw new DuplicateResourceException("Book with ISBN " + updatedBook.getIsbn() + " already exists");
            });
        }

        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setGenre(updatedBook.getGenre());
        existing.setPublishedYear(updatedBook.getPublishedYear());

        // Adjust available copies if totalCopies changed
        int diff = updatedBook.getTotalCopies() - existing.getTotalCopies();
        existing.setTotalCopies(updatedBook.getTotalCopies());
        existing.setAvailableCopies(Math.max(0, existing.getAvailableCopies() + diff));

        return bookRepository.save(existing);
    }

    public void deleteBook(String id) {
        log.info("Deleting book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public long getTotalBooks() {
        return bookRepository.count();
    }

    // Called by BookIssueService
    public void decrementAvailableCopies(String bookId) {
        Book book = getBookById(bookId);
        if (book.getAvailableCopies() <= 0) {
            throw new com.library.exception.BookNotAvailableException(
                    "No copies available for book: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.CHECKED_OUT);
        }
        bookRepository.save(book);
    }

    public void incrementAvailableCopies(String bookId) {
        Book book = getBookById(bookId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setStatus(Book.BookStatus.AVAILABLE);
        bookRepository.save(book);
    }
}
