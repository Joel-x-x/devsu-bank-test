package com.devsu.bank.reports.service;

import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.account.repository.AccountRepository;
import com.devsu.bank.customer.entity.CustomerEntity;
import com.devsu.bank.customer.repository.CustomerRepository;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import com.devsu.bank.movement.entity.MovementEntity;
import com.devsu.bank.movement.repository.MovementRepository;
import com.devsu.bank.reports.dto.AccountStatementData;
import com.devsu.bank.reports.dto.AccountStatementRequest;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for generating account statements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional(readOnly = true)
    public byte[] generateAccountStatementExcel(AccountStatementRequest request) {
        log.info("Generating Excel account statement for customer: {}", request.customerId());
        
        AccountStatementData data = prepareAccountStatementData(request);
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Estado de Cuenta");
            
            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            int rowNum = 0;
            
            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ESTADO DE CUENTA BANCARIO");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));
            rowNum++;
            
            // Customer info
            Row customerRow = sheet.createRow(rowNum++);
            customerRow.createCell(0).setCellValue("Cliente:");
            customerRow.createCell(1).setCellValue(data.getCustomerName());
            
            Row idRow = sheet.createRow(rowNum++);
            idRow.createCell(0).setCellValue("Identificación:");
            idRow.createCell(1).setCellValue(data.getCustomerIdentification());
            
            Row periodRow = sheet.createRow(rowNum++);
            periodRow.createCell(0).setCellValue("Período:");
            periodRow.createCell(1).setCellValue(data.getStartDate() + " al " + data.getEndDate());
            
            rowNum++;
            
            // For each account
            for (AccountStatementData.AccountData account : data.getAccounts()) {
                // Account header
                Row accountHeaderRow = sheet.createRow(rowNum++);
                accountHeaderRow.createCell(0).setCellValue("Cuenta: " + account.getAccountNumber() + " - " + account.getAccountType());
                accountHeaderRow.createCell(3).setCellValue("Saldo Inicial:");
                accountHeaderRow.createCell(4).setCellValue(account.getInitialBalance().doubleValue());
                
                // Movements table header
                Row headerRow = sheet.createRow(rowNum++);
                String[] headers = {"Fecha", "Tipo", "Monto", "Saldo", "Disponible"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // Movements data
                for (AccountStatementData.MovementData movement : account.getMovements()) {
                    Row dataRow = sheet.createRow(rowNum++);
                    dataRow.createCell(0).setCellValue(movement.getDate().format(DATETIME_FORMATTER));
                    dataRow.createCell(1).setCellValue(movement.getType());
                    dataRow.createCell(2).setCellValue(movement.getAmount().doubleValue());
                    dataRow.createCell(3).setCellValue(movement.getBalance().doubleValue());
                    dataRow.createCell(4).setCellValue(movement.getAvailableBalance().doubleValue());
                }
                
                // Final balance
                Row finalBalanceRow = sheet.createRow(rowNum++);
                finalBalanceRow.createCell(3).setCellValue("Saldo Final:");
                Cell finalBalanceCell = finalBalanceRow.createCell(4);
                finalBalanceCell.setCellValue(account.getFinalBalance().doubleValue());
                finalBalanceCell.setCellStyle(headerStyle);
                
                rowNum++;
            }
            
            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            log.info("Excel report generated successfully");
            return out.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating Excel report", e);
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] generateAccountStatementPdf(AccountStatementRequest request) {
        log.info("Generating PDF account statement for customer: {}", request.customerId());
        
        AccountStatementData data = prepareAccountStatementData(request);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Title
            Paragraph title = new Paragraph("ESTADO DE CUENTA BANCARIO")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            document.add(new Paragraph("\n"));
            
            // Customer info
            document.add(new Paragraph("Cliente: " + data.getCustomerName()).setFontSize(12));
            document.add(new Paragraph("Identificación: " + data.getCustomerIdentification()).setFontSize(12));
            document.add(new Paragraph("Período: " + data.getStartDate() + " al " + data.getEndDate()).setFontSize(12));
            document.add(new Paragraph("Fecha de generación: " + data.getReportDate().format(DATETIME_FORMATTER)).setFontSize(10));
            
            document.add(new Paragraph("\n"));
            
            // For each account
            for (AccountStatementData.AccountData account : data.getAccounts()) {
                // Account header
                Paragraph accountHeader = new Paragraph("Cuenta: " + account.getAccountNumber() + " - " + account.getAccountType())
                        .setFontSize(14)
                        .setBold();
                document.add(accountHeader);
                
                document.add(new Paragraph("Saldo Inicial: $" + account.getInitialBalance()).setFontSize(11));
                document.add(new Paragraph("Límite Diario: $" + account.getDailyLimit()).setFontSize(11));
                
                // Movements table
                float[] columnWidths = {150, 80, 100, 100, 100};
                Table table = new Table(UnitValue.createPointArray(columnWidths));
                table.setWidth(UnitValue.createPercentValue(100));
                
                // Header
                String[] headers = {"Fecha", "Tipo", "Monto", "Saldo", "Disponible"};
                for (String header : headers) {
                    table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph(header).setBold())
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER));
                }
                
                // Data
                for (AccountStatementData.MovementData movement : account.getMovements()) {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(movement.getDate().format(DATETIME_FORMATTER))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(movement.getType())).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + movement.getAmount())).setTextAlignment(TextAlignment.RIGHT));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + movement.getBalance())).setTextAlignment(TextAlignment.RIGHT));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + movement.getAvailableBalance())).setTextAlignment(TextAlignment.RIGHT));
                }
                
                document.add(table);
                
                // Final balance
                Paragraph finalBalance = new Paragraph("Saldo Final: $" + account.getFinalBalance())
                        .setFontSize(12)
                        .setBold()
                        .setTextAlignment(TextAlignment.RIGHT);
                document.add(finalBalance);
                
                document.add(new Paragraph("\n"));
            }
            
            document.close();
            log.info("PDF report generated successfully");
            return out.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    /**
     * Prepares the data structure for the report.
     */
    private AccountStatementData prepareAccountStatementData(AccountStatementRequest request) {
        // Get customer
        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer", request.customerId()));
        
        // Get customer's accounts (exclude deleted accounts)
        List<AccountEntity> accounts = accountRepository.findByCustomerId(request.customerId())
                .stream()
                .filter(account -> account.getDeletedAt() == null)
                .toList();
        
        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("No active accounts found for customer: " + request.customerId());
        }
        
        // Prepare date range
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime = request.endDate().atTime(LocalTime.MAX);
        
        // Build account data with movements
        List<AccountStatementData.AccountData> accountDataList = accounts.stream()
                .map(account -> {
                    // Get movements for this account in the date range
                    List<MovementEntity> movements = movementRepository
                            .findByAccountIdAndMovementDateBetweenOrderByMovementDateDesc(
                                    account.getId(),
                                    startDateTime,
                                    endDateTime
                            );
                    
                    // Convert to DTO
                    List<AccountStatementData.MovementData> movementDataList = movements.stream()
                            .map(m -> AccountStatementData.MovementData.builder()
                                    .date(m.getMovementDate())
                                    .type(translateMovementType(m.getMovementType().name()))
                                    .amount(m.getAmount())
                                    .balance(m.getBalance())
                                    .availableBalance(m.getAvailableBalance())
                                    .build())
                            .collect(Collectors.toList());
                    
                    // Calculate final balance
                    java.math.BigDecimal finalBalance = movements.isEmpty()
                            ? account.getInitialBalance()
                            : movements.get(0).getBalance(); // First movement (most recent) has final balance
                    
                    return AccountStatementData.AccountData.builder()
                            .accountNumber(account.getAccountNumber())
                            .accountType(translateAccountType(account.getAccountType().name(), account.getStatus()))
                            .initialBalance(account.getInitialBalance())
                            .finalBalance(finalBalance)
                            .dailyLimit(account.getDailyLimit())
                            .movements(movementDataList)
                            .build();
                })
                .collect(Collectors.toList());
        
        return AccountStatementData.builder()
                .customerName(customer.getName())
                .customerIdentification(customer.getIdentification())
                .reportDate(LocalDateTime.now())
                .startDate(request.startDate().format(DATE_FORMATTER))
                .endDate(request.endDate().format(DATE_FORMATTER))
                .accounts(accountDataList)
                .build();
    }
    
    // Excel style helpers
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }
    
    /**
     * Translates account type to Spanish.
     */
    private String translateAccountType(String accountType, Boolean status) {
        String translatedType = switch (accountType) {
            case "SAVINGS" -> "Ahorros";
            case "CHECKING" -> "Corriente";
            default -> accountType;
        };
        
        // Add inactive indicator if account is not active
        if (status == null || !status) {
            translatedType += " (INACTIVA)";
        }
        
        return translatedType;
    }
    
    /**
     * Translates movement type to Spanish.
     */
    private String translateMovementType(String movementType) {
        return switch (movementType) {
            case "CREDIT" -> "Crédito";
            case "DEBIT" -> "Débito";
            default -> movementType;
        };
    }
}
