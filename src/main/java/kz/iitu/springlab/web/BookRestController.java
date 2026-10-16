package kz.iitu.springlab.web;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    // GET /api/books?author=...&limit=...  -> 200
    @GetMapping
    public List<Book> list(@RequestParam(required = false) String author,
                           @RequestParam(defaultValue = "10") int limit) {
        return service.findAll(author).stream().limit(limit).toList();
    }

    // Individual assignment, variant 11: GET /api/books/sorted?sort=title|author|year
    // The literal path "/sorted" is more specific than "/{id}", so it wins the mapping.
    @GetMapping("/sorted")
    public List<Book> sorted(@RequestParam(defaultValue = "title") String sort) {
        Comparator<Book> comparator = switch (sort) {
            case "title" -> Comparator.comparing(Book::title, String.CASE_INSENSITIVE_ORDER);
            case "author" -> Comparator.comparing(Book::author, String.CASE_INSENSITIVE_ORDER);
            case "year" -> Comparator.comparingInt(Book::year);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unknown sort value: " + sort + " (allowed: title, author, year)");
        };
        return service.findAll(null).stream().sorted(comparator).toList();
    }

    // GET /api/books/{id}  -> 200 or 404
    @GetMapping("/{id}")
    public ResponseEntity<Book> find(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/books  -> 201 Created + Location
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = service.create(book);
        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(saved);
    }

    // PUT /api/books/{id}  -> 200 or 404
    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(@PathVariable long id, @RequestBody Book book) {
        return service.replace(id, book)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /api/books/{id}  -> 204 or 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
