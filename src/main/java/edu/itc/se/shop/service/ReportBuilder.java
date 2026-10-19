package edu.itc.se.shop.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // A NEW instance is created on EVERY getObject() call!
public class ReportBuilder {
    public String buildReport() {
        return "Report generated at " + System.currentTimeMillis();
    }
}