package org.DPT.users.pt.model;

import java.time.LocalDate;

// Record di supporto per il report
public record PerformanceDTO(String clientName, LocalDate date, int duration, int completionPercentage)
{ }
