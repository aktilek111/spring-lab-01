package kz.iitu.springlab.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    /** All books when author is null/blank; otherwise a case-insensitive "contains" filter. */
    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }
        String needle = author.toLowerCase();
        return repository.findAll().stream()
                .filter(b -> b.author().toLowerCase().contains(needle))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    public Book create(Book book) {
        // the identifier is always assigned by the repository, never by the client
        return repository.save(new Book(null, book.title(), book.author(), book.year()));
    }

    /** Replaces the record with the given id; empty if there is no such record. */
    public Optional<Book> replace(long id, Book book) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(repository.save(new Book(id, book.title(), book.author(), book.year())));
    }

    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}
