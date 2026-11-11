package kz.iitu.txlab.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kz.iitu.txlab.service.AccountService;

@RestController
@RequestMapping("/api")
public class AccountController {

    public record AccountView(Long id, String ownerName, BigDecimal balance, Long version) { }

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts")
    public List<AccountView> accounts() {
        return service.findAll().stream()
                .map(a -> new AccountView(a.getId(), a.getOwnerName(), a.getBalance(), a.getVersion()))
                .toList();
    }

    @PostMapping("/transfer")
    public List<AccountView> transfer(@RequestParam Long from, @RequestParam Long to,
                                      @RequestParam BigDecimal amount) {
        service.transfer(from, to, amount);
        return accounts();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> badRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
