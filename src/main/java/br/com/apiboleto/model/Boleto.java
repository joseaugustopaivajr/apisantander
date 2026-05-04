package br.com.apiboleto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "boletos")
@Getter
@Setter
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioSolicitante;

    private String environment;
    private String nsuCode;
    private LocalDate nsuDate;
    private String covenantCode;
    private String bankNumber;
    private String clientNumber;
    private LocalDate dueDate;
    private LocalDate issueDate;
    private String participantCode;
    private BigDecimal nominalValue;

    @Column(name = "payer_name")
    private String payerName;
    @Column(name = "payer_document_type")
    private String payerDocumentType;
    @Column(name = "payer_document_number")
    private String payerDocumentNumber;

    private String barcode;
    private String digitableLine;
    private LocalDate entryDate;

    @Column(length = 1000)
    private String qrCodePix;
    @Column(length = 2048)
    private String qrCodeUrl;
    @Column(length = 2048)
    private String urlBoleto;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
