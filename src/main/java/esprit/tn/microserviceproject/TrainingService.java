package esprit.tn.microserviceproject;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
public class TrainingService {


    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training addTraining(Training training) {
        return trainingRepository.save(training);
    }

    public List<Training> getAll(){
        return trainingRepository.findAll();
    }

    public Training getTrainingById(int id) {
        return trainingRepository.findById(id).orElse(null);
    }

    public String deleteTraining(int id) {
        if (trainingRepository.findById(id).isPresent()) {
            trainingRepository.deleteById(id);
            return "Training supprimé";
        } else
            return "Training non supprimé";
    }
    public Training updateTraining(int id, Training newTraining) {
        if (trainingRepository.findById(id).isPresent()) {

            Training existingTraining = trainingRepository.findById(id).get();
            existingTraining.setTitle(newTraining.getTitle());
            existingTraining.setLevel(newTraining.getLevel());
            existingTraining.setDescription(newTraining.getDescription());
            existingTraining.setTypeTraining(newTraining.getTypeTraining());

            return trainingRepository.save(existingTraining);
        } else
            return null;
    }

    public List<Training> getTrainingsByLevel(String level) {
        return trainingRepository.findByLevel(level);
    }

    public List<Training> getTrainingsByType(TypeTraining typeTraining) {
        return trainingRepository.findByTypeTraining(typeTraining);
    }

    public List<Training> searchTrainingsByTitle(String keyword) {
        return trainingRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Training> getTrainingsByLevelAndType(String level, TypeTraining typeTraining) {
        return trainingRepository.findByLevelAndTypeTraining(level, typeTraining);
    }


    public Page<Training> getTrainingsWithPaginationAndSorting(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return trainingRepository.findAll(pageable);
    }

    public List<Training> searchTrainingsByTitleAndLevel(String keyword, String level) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndLevel(keyword, level);
    }

    public List<Training> searchTrainingsByTitleAndType(String keyword, TypeTraining typeTraining) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndTypeTraining(keyword, typeTraining);
    }

    public List<Training> searchByTitleLevelAndType(String keyword, String level, TypeTraining type) {
        return trainingRepository.findByTitleContainingIgnoreCaseAndLevelAndTypeTraining(keyword, level, type);
    }


    public ByteArrayInputStream exportTrainingsToPDF(List<Training> trainings) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Liste des Formations Filtrées", titleFont));
            document.add(new Paragraph(" ")); // espace

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Header
            table.addCell(new Phrase("Title", headerFont));
            table.addCell(new Phrase("Level", headerFont));
            table.addCell(new Phrase("Description", headerFont));
            table.addCell(new Phrase("Type", headerFont));

            // Body
            for (Training t : trainings) {
                table.addCell(new Phrase(t.getTitle(), bodyFont));
                table.addCell(new Phrase(t.getLevel(), bodyFont));
                table.addCell(new Phrase(t.getDescription(), bodyFont));
                table.addCell(new Phrase(t.getTypeTraining().toString(), bodyFont));
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    public String getTrainingReport() {
        StringBuilder report = new StringBuilder();

        report.append("Nombre de formations par type :\n");
        List<Object[]> typeCounts = trainingRepository.countTrainingsByType();
        for (Object[] row : typeCounts) {
            report.append("- ").append(row[0]).append(" : ").append(row[1]).append("\n");
        }

        report.append("\nNombre de formations par niveau :\n");
        List<Object[]> levelCounts = trainingRepository.countTrainingsByLevel();
        for (Object[] row : levelCounts) {
            report.append("- ").append(row[0]).append(" : ").append(row[1]).append("\n");
        }

        return report.toString();
    }


    public ByteArrayInputStream generateTrainingSummaryPDF() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Rapport des Formations", titleFont));
            document.add(new Paragraph(" ")); // espace

            // Formations par type
            document.add(new Paragraph("Nombre de formations par type :", sectionFont));
            List<Object[]> typeCounts = trainingRepository.countTrainingsByType();
            for (Object[] row : typeCounts) {
                String line = "- " + row[0] + " : " + row[1];
                document.add(new Paragraph(line, textFont));
            }

            document.add(new Paragraph(" ")); // espace

            // Formations par niveau
            document.add(new Paragraph("Nombre de formations par niveau :", sectionFont));
            List<Object[]> levelCounts = trainingRepository.countTrainingsByLevel();
            for (Object[] row : levelCounts) {
                String line = "- " + row[0] + " : " + row[1];
                document.add(new Paragraph(line, textFont));
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    public List<Training> getTrainingsBetweenDates(LocalDate start, LocalDate end) {
        return trainingRepository.findByStartDateBetween(start, end);
    }





    public String getMonthlyTrainingReport(LocalDate start, LocalDate end) {
        List<Training> trainings = trainingRepository.findByStartDateBetween(start, end);

        if (trainings.isEmpty()) {
            return "Aucune formation trouvée entre " + start + " et " + end;
        }

        Map<String, List<String>> trainingsGrouped = trainings.stream()
                .collect(Collectors.groupingBy(
                        t -> {
                            Month month = t.getStartDate().getMonth();
                            int year = t.getStartDate().getYear();
                            return month.getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + year;
                        },
                        Collectors.mapping(Training::getTitle, Collectors.toList())
                ));

        StringBuilder report = new StringBuilder();
        trainingsGrouped.forEach((mois, titres) -> {
            report.append("📅 ").append(mois).append(" :\n");
            titres.forEach(title -> report.append("- ").append(title).append("\n"));
            report.append("\n");
        });

        return report.toString();
    }


    public ByteArrayInputStream generateMonthlyReportPDF(LocalDate start, LocalDate end) {
        List<Training> trainings = trainingRepository.findByStartDateBetween(start, end);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Rapport Mensuel des Formations", titleFont));
            document.add(new Paragraph("Période : " + start + " → " + end));
            document.add(new Paragraph(" "));

            if (trainings.isEmpty()) {
                document.add(new Paragraph("Aucune formation trouvée entre ces dates.", textFont));
            } else {
                Map<String, List<String>> trainingsGrouped = trainings.stream()
                        .collect(Collectors.groupingBy(
                                t -> {
                                    Month month = t.getStartDate().getMonth();
                                    int year = t.getStartDate().getYear();
                                    return month.getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + year;
                                },
                                TreeMap::new,
                                Collectors.mapping(Training::getTitle, Collectors.toList())
                        ));

                for (Map.Entry<String, List<String>> entry : trainingsGrouped.entrySet()) {
                    document.add(new Paragraph("📅 " + entry.getKey(), sectionFont));
                    for (String title : entry.getValue()) {
                        document.add(new Paragraph("- " + title, textFont));
                    }
                    document.add(new Paragraph(" "));
                }
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Transactional
    public String deletePastTrainings() {
        LocalDate today = LocalDate.now();
        trainingRepository.deleteByEndDateBefore(today);
        return "Toutes les formations passées ont été supprimées.";
    }


}
