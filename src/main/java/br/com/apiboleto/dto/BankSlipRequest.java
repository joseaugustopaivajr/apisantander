package br.com.apiboleto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Requisição para emissão de boleto bancário")
public class BankSlipRequest {
    @Schema(description = "Ambiente da requisição", example = "PROCUCAO")
    private String environment;
    @Schema(description = "Código NSU", example = "12345678901234567000")
    private Long nsuCode;
    @Schema(description = "Data do NSU", example = "2022-12-12")
    private String nsuDate;
    @Schema(description = "Código do convênio", example = "1234567")
    private String covenantCode;
    @Schema(description = "Número do banco", example = "123")
    private String bankNumber;
    @Schema(description = "Número do cliente", example = "fd119Dc4d48F460")
    private String clientNumber;
    @Schema(description = "Data de vencimento", example = "2022-12-12")
    private String dueDate;
    @Schema(description = "Data de emissão", example = "2022-12-12")
    private String issueDate;
    @Schema(description = "Código do participante", example = "registro1234567890")
    private String participantCode;
    @Schema(description = "Valor nominal", example = "10.15")
    private String nominalValue;
    private Payer payer;
    private Beneficiary beneficiary;
    @Schema(description = "Espécie do documento", example = "DUPLICATA_MERCANTIL")
    private String documentKind;
    private Discount discount;
    @Schema(description = "Percentual de multa", example = "97.80")
    private String finePercentage;
    @Schema(description = "Quantidade de dias para multa", example = "5")
    private String fineQuantityDays;
    @Schema(description = "Percentual de juros", example = "5.00")
    private String interestPercentage;
    @Schema(description = "Valor de dedução", example = "10.00")
    private String deductionValue;
    @Schema(description = "Tipo de protesto", example = "SEM_PROTESTO")
    private String protestType;
    @Schema(description = "Quantidade de dias para protesto", example = "32")
    private String protestQuantityDays;
    @Schema(description = "Quantidade de dias para baixa", example = "32")
    private String writeOffQuantityDays;
    @Schema(description = "Tipo de pagamento", example = "REGISTRO")
    private String paymentType;
    @Schema(description = "Quantidade de parcelas", example = "32")
    private String parcelsQuantity;
    @Schema(description = "Tipo de valor", example = "PERCENTUAL")
    private String valueType;
    @Schema(description = "Valor ou percentual mínimo", example = "32.06")
    private String minValueOrPercentage;
    @Schema(description = "Valor ou percentual máximo", example = "49.36")
    private String maxValueOrPercentage;
    @Schema(description = "Percentual de IOF", example = "32.45325")
    private String iofPercentage;
    private List<Sharing> sharing;
    private Key key;
    @Schema(description = "ID da transação Pix", example = "1234567890abcdefghij123456")
    private String txId;
    @Schema(description = "Mensagens do boleto", example = "[\"mensagem um\", \"mensagem dois\"]")
    private List<String> messages;

    @Data
    @Schema(description = "Dados do pagador")
    public static class Payer {
        @Schema(description = "Nome do pagador", example = "João da Silva")
        private String name;
        @Schema(description = "Tipo de documento", example = "CPF")
        private String documentType;
        @Schema(description = "Número do documento", example = "9615865832")
        private Long documentNumber;
        @Schema(description = "Endereço", example = "Rua XV de Maio")
        private String address;
        @Schema(description = "Bairro", example = "Vila Industrial")
        private String neighborhood;
        @Schema(description = "Cidade", example = "São Paulo")
        private String city;
        @Schema(description = "Estado", example = "SP")
        private String state;
        @Schema(description = "CEP", example = "09761-233")
        private String zipCode;
    }

    @Data
    @Schema(description = "Dados do beneficiário")
    public static class Beneficiary {
        @Schema(description = "Nome do beneficiário", example = "João da Silva")
        private String name;
        @Schema(description = "Tipo de documento", example = "CPF")
        private String documentType;
        @Schema(description = "Número do documento", example = "9615865832")
        private Long documentNumber;
    }

    @Data
    @Schema(description = "Dados de desconto")
    public static class Discount {
        @Schema(description = "Tipo de desconto", example = "VALOR_DATA_FIXA")
        private String type;
        private DiscountDetail discountOne;
        private DiscountDetail discountTwo;
        private DiscountDetail discountThree;
    }

    @Data
    @Schema(description = "Detalhes do desconto")
    public static class DiscountDetail {
        @Schema(description = "Valor do desconto", example = "5.5")
        private Double value;
        @Schema(description = "Data limite do desconto", example = "2022-12-12")
        private String limitDate;
    }

    @Data
    @Schema(description = "Dados de rateio")
    public static class Sharing {
        @Schema(description = "Código", example = "12")
        private String code;
        @Schema(description = "Valor", example = "132.5")
        private String value;
    }

    @Data
    @Schema(description = "Chave Pix")
    public static class Key {
        @Schema(description = "Tipo de chave", example = "CPF")
        private String type;
        @Schema(description = "Valor da chave", example = "09463589723")
        private String dictKey;
    }
}
