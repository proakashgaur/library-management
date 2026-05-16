package com.library.repository;

import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory Book Store — replace this with JPA/DB later.
 * Uses ConcurrentHashMap for thread safety.
 */
@Repository
public class BookRepository {

    private final Map<String, Book> store = new ConcurrentHashMap<>();

    public BookRepository() {
        seedData();
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID().toString());
            book.setCreatedAt(LocalDateTime.now());
            book.setAvailableCopies(book.getTotalCopies());
            book.setStatus(Book.BookStatus.AVAILABLE);
        }
        book.setUpdatedAt(LocalDateTime.now());
        store.put(book.getId(), book);
        return book;
    }

    public Optional<Book> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Book> findByIsbn(String isbn) {
        return store.values().stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Book> findByGenre(String genre) {
        return store.values().stream()
                .filter(b -> b.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public List<Book> findByAuthor(String author) {
        return store.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> search(String keyword) {
        String kw = keyword.toLowerCase();
        return store.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(kw)
                        || b.getAuthor().toLowerCase().contains(kw)
                        || b.getIsbn().toLowerCase().contains(kw)
                        || b.getGenre().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Book> findAvailable() {
        return store.values().stream()
                .filter(b -> b.getAvailableCopies() > 0)
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public long count() {
        return store.size();
    }

    // ── Seed Data ─────────────────────────────────────────────────────────────

    private void seedData() {
        List<Book> books = List.of(
            Book.builder().id(uuid()).title("Clean Code").author("Robert C. Martin")
                .isbn("978-0132350884").genre("Programming").publishedYear(2008)
                .totalCopies(5).availableCopies(3).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("The Pragmatic Programmer").author("Andrew Hunt")
                .isbn("978-0201616224").genre("Programming").publishedYear(1999)
                .totalCopies(4).availableCopies(4).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Designing Data-Intensive Applications").author("Martin Kleppmann")
                .isbn("978-1449373320").genre("Software Engineering").publishedYear(2017)
                .totalCopies(3).availableCopies(1).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Spring Boot in Action").author("Craig Walls")
                .isbn("978-1617292545").genre("Java").publishedYear(2016)
                .totalCopies(6).availableCopies(6).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Microservices Patterns").author("Chris Richardson")
                .isbn("978-1617294549").genre("Software Engineering").publishedYear(2018)
                .totalCopies(4).availableCopies(2).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Docker Deep Dive").author("Nigel Poulton")
                .isbn("978-1521822807").genre("DevOps").publishedYear(2018)
                .totalCopies(5).availableCopies(5).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Kubernetes in Action").author("Marko Luksa")
                .isbn("978-1617293726").genre("DevOps").publishedYear(2017)
                .totalCopies(3).availableCopies(0).status(Book.BookStatus.CHECKED_OUT)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("The Phoenix Project").author("Gene Kim")
                .isbn("978-0988262508").genre("DevOps").publishedYear(2013)
                .totalCopies(4).availableCopies(3).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("Java: The Complete Reference").author("Herbert Schildt")
                .isbn("978-1260440232").genre("Java").publishedYear(2020)
                .totalCopies(7).availableCopies(5).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),

            Book.builder().id(uuid()).title("System Design Interview").author("Alex Xu")
                .isbn("978-1736049105").genre("Software Engineering").publishedYear(2020)
                .totalCopies(5).availableCopies(4).status(Book.BookStatus.AVAILABLE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );

        books.forEach(b -> store.put(b.getId(), b));
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
