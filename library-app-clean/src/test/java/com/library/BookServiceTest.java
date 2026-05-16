package com.library;

import com.library.exception.BookNotAvailableException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setup() {
        sampleBook = Book.builder()
                .id("book-1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .genre("Programming")
                .publishedYear(2008)
                .totalCopies(5)
                .availableCopies(5)
                .status(Book.BookStatus.AVAILABLE)
                .build();
    }

    @Test
    @DisplayName("Should add a new book successfully")
    void addBook_success() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        Book result = bookService.addBook(sampleBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate ISBN")
    void addBook_duplicateIsbn_throwsException() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(sampleBook));

        assertThatThrownBy(() -> bookService.addBook(sampleBook))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("978-0132350884");
    }

    @Test
    @DisplayName("Should return book by ID")
    void getBookById_found() {
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(sampleBook));

        Book result = bookService.getBookById("book-1");

        assertThat(result.getId()).isEqualTo("book-1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for unknown ID")
    void getBookById_notFound() {
        when(bookRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return all books")
    void getAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        List<Book> books = bookService.getAllBooks();

        assertThat(books).hasSize(1);
    }

    @Test
    @DisplayName("Should throw when decrementing 0 available copies")
    void decrementAvailableCopies_noStock_throwsException() {
        sampleBook.setAvailableCopies(0);
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(sampleBook));

        assertThatThrownBy(() -> bookService.decrementAvailableCopies("book-1"))
                .isInstanceOf(BookNotAvailableException.class);
    }

    @Test
    @DisplayName("Should delete book successfully")
    void deleteBook_success() {
        when(bookRepository.existsById("book-1")).thenReturn(true);
        doNothing().when(bookRepository).deleteById("book-1");

        assertThatCode(() -> bookService.deleteBook("book-1")).doesNotThrowAnyException();
        verify(bookRepository).deleteById("book-1");
    }
}
