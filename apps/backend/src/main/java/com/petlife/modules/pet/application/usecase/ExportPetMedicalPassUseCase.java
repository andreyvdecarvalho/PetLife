package com.petlife.modules.pet.application.usecase;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.petlife.modules.auth.domain.entity.UserPlan;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.TimelineEventResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExportPetMedicalPassUseCase {

    private final PetRepositoryPort petRepositoryPort;
    private final GetPetTimelineUseCase getPetTimelineUseCase;

    @Transactional(readOnly = true)
    public byte[] execute(UUID petId, UUID userId, LocalDate startDate, LocalDate endDate) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        // Validate plan - only PREMIUM or FAMILY subscriptions
        if (pet.getUser().getPlan() == UserPlan.FREE) {
            throw BusinessException.forbidden("PREMIUM_REQUIRED",
                    "A exportação de prontuário em PDF é um recurso premium.");
        }

        // Fetch all timeline events (unpaginated - we pass page 0 and a very large size)
        List<TimelineEventResponse> events = getPetTimelineUseCase.execute(petId, userId, null, 0, Integer.MAX_VALUE);

        // Filter by date range if provided
        if (startDate != null) {
            OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            events = events.stream()
                    .filter(e -> e.getDate() != null && !e.getDate().isBefore(startDateTime))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            OffsetDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            events = events.stream()
                    .filter(e -> e.getDate() != null && e.getDate().isBefore(endDateTime))
                    .collect(Collectors.toList());
        }

        return generatePdf(pet, events);
    }

    private byte[] generatePdf(Pet pet, List<TimelineEventResponse> events) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Font configurations
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, java.awt.Color.DARK_GRAY);
            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, java.awt.Color.BLACK);
            Font boldBodyFont = new Font(Font.HELVETICA, 10, Font.BOLD, java.awt.Color.BLACK);
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, java.awt.Color.DARK_GRAY);

            // Capa / Cabeçalho
            Paragraph title = new Paragraph("Passaporte Clínico - PetLife", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Pet Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            infoTable.addCell(createCell("Nome:", boldBodyFont));
            infoTable.addCell(createCell(pet.getName(), bodyFont));

            infoTable.addCell(createCell("Espécie / Raça:", boldBodyFont));
            String breed = pet.getBreed() != null ? pet.getBreed() : "N/A";
            infoTable.addCell(createCell(pet.getSpecies() + " / " + breed, bodyFont));

            infoTable.addCell(createCell("Sexo:", boldBodyFont));
            infoTable.addCell(createCell(pet.getSex() != null ? pet.getSex().name() : "N/A", bodyFont));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            infoTable.addCell(createCell("Data de Nascimento:", boldBodyFont));
            String birthDateStr = pet.getBirthDate() != null
                    ? pet.getBirthDate().format(formatter) : "Não informada";
            infoTable.addCell(createCell(birthDateStr, bodyFont));

            infoTable.addCell(createCell("Microchip ID:", boldBodyFont));
            infoTable.addCell(createCell(pet.getMicrochipId() != null ? pet.getMicrochipId() : "N/A", bodyFont));

            infoTable.addCell(createCell("Alergias:", boldBodyFont));
            String allergies = pet.getAllergies() != null ? pet.getAllergies() : "Nenhuma registrada";
            infoTable.addCell(createCell(allergies, bodyFont));

            infoTable.addCell(createCell("Notas:", boldBodyFont));
            infoTable.addCell(createCell(pet.getNotes() != null ? pet.getNotes() : "N/A", bodyFont));

            document.add(infoTable);

            // Timeline Header
            Paragraph sectionTitle = new Paragraph("Histórico Clínico e Eventos", sectionFont);
            sectionTitle.setSpacingAfter(10);
            document.add(sectionTitle);

            if (events.isEmpty()) {
                Paragraph noEvents = new Paragraph("Nenhum evento registrado no período selecionado.", bodyFont);
                document.add(noEvents);
            } else {
                // Events Table
                PdfPTable eventsTable = new PdfPTable(new float[]{1.5f, 1.5f, 4f, 4f});
                eventsTable.setWidthPercentage(100);
                eventsTable.setSpacingAfter(10);

                eventsTable.addCell(createHeaderCell("Data", boldBodyFont));
                eventsTable.addCell(createHeaderCell("Tipo", boldBodyFont));
                eventsTable.addCell(createHeaderCell("Título", boldBodyFont));
                eventsTable.addCell(createHeaderCell("Descrição", boldBodyFont));

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (TimelineEventResponse event : events) {
                    String dateStr = event.getDate() != null ? event.getDate().format(dateTimeFormatter) : "N/A";
                    eventsTable.addCell(createCell(dateStr, bodyFont));
                    eventsTable.addCell(createCell(event.getType().name(), bodyFont));
                    eventsTable.addCell(createCell(event.getTitle(), bodyFont));
                    String desc = event.getDescription() != null ? event.getDescription() : "";
                    eventsTable.addCell(createCell(desc, bodyFont));
                }

                document.add(eventsTable);
            }

            document.close();
        } catch (DocumentException e) {
            throw new BusinessException("PDF_GENERATION_FAILED", "Falha ao gerar o prontuário em PDF.",
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return baos.toByteArray();
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
