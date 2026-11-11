package kz.iitu.txlab.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log")
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AuditRecord() { }

    public AuditRecord(String message) {
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getMessage() { return message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
