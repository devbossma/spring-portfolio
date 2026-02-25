package dev.saberlabs.myspringportfolio.investment;

import java.time.LocalDate;

public record Investment(
        Integer id,
        String name,
        String description,
        Double amount,
        LocalDate startDate,
        LocalDate endDate,
        String type,
        String status,
        String riskLevel,
        String expectedReturn,
        String actualReturn,
        String notes,
        LocalDate createdAt,
        LocalDate updatedAt
) {

}
