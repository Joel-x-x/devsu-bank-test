package com.devsu.bank.reports.service;

import com.devsu.bank.reports.dto.AccountStatementRequest;

/**
 * Service interface for generating reports.
 */
public interface ReportService {
    
    /**
     * Generates an account statement in Excel format.
     * 
     * @param request The report parameters (customerId, date range)
     * @return Excel file as byte array
     */
    byte[] generateAccountStatementExcel(AccountStatementRequest request);
    
    /**
     * Generates an account statement in PDF format.
     * 
     * @param request The report parameters (customerId, date range)
     * @return PDF file as byte array
     */
    byte[] generateAccountStatementPdf(AccountStatementRequest request);
}
