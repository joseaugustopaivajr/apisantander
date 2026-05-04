package br.com.apiboleto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Resposta da emissão de boleto bancário")
public class BankSlipResponse extends BankSlipRequest {
    @Schema(description = "Código de barras", example = "03396939700000001009356720600000000123450101")
    private String barcode;
    @Schema(description = "Linha digitável", example = "03399356782060000000201234501011693970000000100")
    private String digitableLine;
    @Schema(description = "Data de entrada", example = "2022-12-12")
    private String entryDate;
    @Schema(description = "Conteúdo do QR Code Pix", example = "00020101021226920014br.gov.bcb.pix2570pix.santander.com.br/qr/v2/cobv/9fa03dbd-0b9c-4910-8ab3-14f6bf48a24652040000530398654041.005802BR5925TESTE CONECTIVIDADE API 16009SAO PAULO62070503***63041110")
    private String qrCodePix;
    @Schema(description = "URL do QR Code Pix", example = "pix.santander.com.br/qr/v2/cobv/9fa03dbd-0b9c-4910-8ab3-14f6bf48a246")
    private String qrCodeUrl;
    @Schema(description = "URL para download do boleto em PDF", example = "https://trust-open.api.santander.com.br/collection_bill_management/v2/bills/123/bank_slips/download")
    private String urlBoleto;
}
