package kz.iitu.springlab.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String author, Model model) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");
        return "books/list"; // -> templates/books/list.html
    }

    // Post/Redirect/Get: create, store a flash message, redirect (302), the browser then does a GET
    @PostMapping
    public String create(@RequestParam String title,
                         @RequestParam String author,
                         @RequestParam int year,
                         RedirectAttributes redirect) {
        Book saved = service.create(new Book(null, title, author, year));
        redirect.addFlashAttribute("message", "Book «" + saved.title() + "» has been added");
        return "redirect:/books";
    }
}
