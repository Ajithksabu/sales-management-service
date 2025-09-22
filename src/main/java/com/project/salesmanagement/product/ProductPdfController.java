package com.project.salesmanagement.product;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.salesmanagement.sale.SaleRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class ProductPdfController {
  private final ProductService productService;
  private final SaleRepository saleRepo;

  @GetMapping(value = "/products", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> exportProducts() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4.rotate());
    PdfWriter.getInstance(doc, baos);
    doc.open();
    doc.add(new Paragraph("Product Table"));
    doc.add(new Paragraph(" "));

    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{1.2f, 2.5f, 4f, 1.5f, 1.2f, 1.8f});

    // header
    table.addCell("id");
    table.addCell("name");
    table.addCell("description");
    table.addCell("price");
    table.addCell("quantity");
    table.addCell("revenue");
    table.setHeaderRows(1);

    for (Product p : productService.getAllProducts(Pageable.unpaged()).getContent()) {
      BigDecimal revenue = saleRepo.revenueByProduct(p.getId());
      if (revenue == null) revenue = BigDecimal.ZERO;

      table.addCell(String.valueOf(p.getId()));
      table.addCell(p.getName());
      table.addCell(p.getDescription() == null ? "" : p.getDescription());
      table.addCell(p.getPrice().toPlainString());
      table.addCell(String.valueOf(p.getQuantity()));
      table.addCell(revenue.toPlainString());
    }

    doc.add(table);
    doc.close();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.attachment().filename("products.pdf").build());
    return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
  }
}
