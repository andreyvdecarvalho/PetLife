package com.petlife.modules.pet.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UpdateVaccinationRequest {

    @NotBlank(message = "Vaccine name is required")
    @Size(max = 200, message = "Vaccine name must be less than 200 characters")
    private String vaccineName;

    @NotNull(message = "Date administered is required")
    private LocalDate dateAdministered;

    private LocalDate nextDoseDate;

    @Size(max = 200)
    private String veterinarian;

    @Size(max = 200)
    private String clinic;

    @Size(max = 100)
    private String batchNumber;

    @Size(max = 100)
    private String manufacturer;

    @Size(max = 500)
    private String proofUrl;

    private String notes;

    private Boolean reminderActive;

    // Getters and Setters

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public LocalDate getDateAdministered() {
        return dateAdministered;
    }

    public void setDateAdministered(LocalDate dateAdministered) {
        this.dateAdministered = dateAdministered;
    }

    public LocalDate getNextDoseDate() {
        return nextDoseDate;
    }

    public void setNextDoseDate(LocalDate nextDoseDate) {
        this.nextDoseDate = nextDoseDate;
    }

    public String getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(String veterinarian) {
        this.veterinarian = veterinarian;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProofUrl() {
        return proofUrl;
    }

    public void setProofUrl(String proofUrl) {
        this.proofUrl = proofUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getReminderActive() {
        return reminderActive;
    }

    public void setReminderActive(Boolean reminderActive) {
        this.reminderActive = reminderActive;
    }
}
