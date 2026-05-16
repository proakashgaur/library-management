package com.library.controller;

import com.library.model.ApiResponse;
import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> addBook(@Valid @RequestBody Book book) {
        Book saved = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book added successfully", saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available) {

        List<Book> books;
        if (search != null && !search.isBlank()) {
            books = bookService.searchBooks(search);
        } else if (genre != null) {
            books = bookService.getBooksByGenre(genre);
        } else if (author != null) {
            books = bookService.getBooksByAuthor(author);
        } else if (Boolean.TRUE.equals(available)) {
            books = bookService.getAvailableBooks();
        } else {
            books = bookService.getAllBooks();
        }

        return ResponseEntity.ok(ApiResponse.success("Books retrieved", books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Book found", bookService.getBookById(id)));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<Book>> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(ApiResponse.success("Book found", bookService.getBookByIsbn(isbn)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(
            @PathVariable String id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(ApiResponse.success("Book updated", bookService.updateBook(id, book)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted", null));
    }
}
