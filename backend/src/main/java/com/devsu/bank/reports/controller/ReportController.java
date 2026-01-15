package com.devsu.bank.reports.controller;

import com.devsu.bank.reports.dto.AccountStatementRequest;
import com.devsu.bank.reports.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * REST controller for generating reports.
 * Provides endpoints to generate account statements in different formats.
 */
@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * Generates an account statement in Excel format.
     * 
     * @param customerId The customer ID
     * @param startDate Start date for the report period
     * @param endDate End date for the report period
     * @return Excel file download
     */
    @GetMapping("/account-statement/excel")
    public ResponseEntity<byte[]> generateAccountStatementExcel(
            @RequestParam UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to generate Excel account statement for customer: {}", customerId);
        
        AccountStatementRequest request = new AccountStatementRequest(customerId, startDate, endDate);
        byte[] excelBytes = reportService.generateAccountStatementExcel(request);
        
        String filename = String.format("estado_cuenta_%s_%s.xlsx",
                customerId,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelBytes.length);
        
        log.info("Excel account statement generated successfully");
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
    
    /**
     * Generates an account statement in PDF format.
     * 
     * @param customerId The customer ID
     * @param startDate Start date for the report period
     * @param endDate End date for the report period
     * @return PDF file download
     */
    @GetMapping("/account-statement/pdf")
    public ResponseEntity<byte[]> generateAccountStatementPdf(
            @RequestParam UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to generate PDF account statement for customer: {}", customerId);
        
        AccountStatementRequest request = new AccountStatementRequest(customerId, startDate, endDate);
        byte[] pdfBytes = reportService.generateAccountStatementPdf(request);
        
        String filename = String.format("estado_cuenta_%s_%s.pdf",
                customerId,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(pdfBytes.length);
        
        log.info("PDF account statement generated successfully");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    /**
     * Alternative endpoint using POST with request body.
     * Useful when you need more complex request parameters.
     * 
     * @param request The account statement request
     * @param format The desired format (excel or pdf)
     * @return File download
     */
    @PostMapping("/account-statement")
    public ResponseEntity<byte[]> generateAccountStatement(
            @Valid @RequestBody AccountStatementRequest request,
            @RequestParam(defaultValue = "pdf") String format) {
        
        log.info("REST request to generate {} account statement for customer: {}", format, request.customerId());
        
        byte[] fileBytes;
        MediaType mediaType;
        String extension;
        
        if ("excel".equalsIgnoreCase(format)) {
            fileBytes = reportService.generateAccountStatementExcel(request);
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
            extension = "xlsx";
        } else {
            fileBytes = reportService.generateAccountStatementPdf(request);
            mediaType = MediaType.APPLICATION_PDF;
            extension = "pdf";
        }
        
        String filename = String.format("estado_cuenta_%s_%s.%s",
                request.customerId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                extension);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(fileBytes.length);
        
        log.info("{} account statement generated successfully", format.toUpperCase());
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }
}
