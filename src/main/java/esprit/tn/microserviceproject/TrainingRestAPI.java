package esprit.tn.microserviceproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainingRestAPI {

    private String hello = "Hello, I'm the training MS 4SAE6";

    @RequestMapping("/helloT")
    public String sayHello() {
        return hello;
    }

    @Autowired
    private TrainingService trainingService;

    // Add a new training
    @PostMapping
    public ResponseEntity<Training> addTraining(@RequestBody Training training) {
        Training createdTraining = trainingService.addTraining(training);
        return new ResponseEntity<>(createdTraining, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Training>> getAll() {
        return new ResponseEntity<>(trainingService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  Training getTrainingById(@PathVariable int id) {
        return trainingService.getTrainingById(id);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTraining(@PathVariable int id) {
        String response = trainingService.deleteTraining(id);
        if (response.equals("Training supprimé")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Training> updateJob(@PathVariable int id, @RequestBody Training newTraining) {
        Training updatedTraining = trainingService.updateTraining(id, newTraining);
        if (updatedTraining != null) {
            return new ResponseEntity<>(updatedTraining, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<?> getTrainingsByLevel(@PathVariable String level) {
        List<Training> trainings = trainingService.getTrainingsByLevel(level);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée pour le niveau : " + level, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }


//    @GetMapping("/type/{type}")
//    public ResponseEntity<List<Training>> getTrainingsByType(@PathVariable TypeTraining type) {
//        List<Training> trainings = trainingService.getTrainingsByType(type);
//        return new ResponseEntity<>(trainings, HttpStatus.OK);
//    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getTrainingsByType(@PathVariable TypeTraining type) {
        List<Training> trainings = trainingService.getTrainingsByType(type);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée pour le type : " + type, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTrainingsByTitle(@RequestParam String keyword) {
        List<Training> trainings = trainingService.searchTrainingsByTitle(keyword);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée contenant : " + keyword, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterTrainings(@RequestParam String level,
                                             @RequestParam TypeTraining type) {
        List<Training> trainings = trainingService.getTrainingsByLevelAndType(level, type);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée pour le niveau '" + level + "' et le type '" + type + "'", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getPaginatedTrainings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "level") String sortBy) {

        Page<Training> trainings = trainingService.getTrainingsWithPaginationAndSorting(page, size, sortBy);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée pour la page demandée.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/searchByTitleAndLevel")
    public ResponseEntity<?> searchTrainingsByTitleAndLevel(@RequestParam String keyword,
                                                            @RequestParam String level) {
        List<Training> trainings = trainingService.searchTrainingsByTitleAndLevel(keyword, level);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée avec le mot-clé '" + keyword +
                    "' et le niveau '" + level + "'", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/searchByTitleAndType")
    public ResponseEntity<?> searchTrainingsByTitleAndType(@RequestParam String keyword,
                                                           @RequestParam TypeTraining type) {
        List<Training> trainings = trainingService.searchTrainingsByTitleAndType(keyword, type);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée avec le mot-clé '" + keyword +
                    "' et le type '" + type + "'", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }


    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> exportFilteredToPDF(@RequestParam String keyword,
                                                 @RequestParam TypeTraining type) {
        List<Training> trainings = trainingService.searchTrainingsByTitleAndType(keyword, type);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée avec le mot-clé '" + keyword +
                    "' et le type '" + type + "'", HttpStatus.NOT_FOUND);
        }

        ByteArrayInputStream bis = trainingService.exportTrainingsToPDF(trainings);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=filtered-trainings.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/export/pdf/advanced", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> exportAdvancedFilteredToPDF(
            @RequestParam String keyword,
            @RequestParam String level,
            @RequestParam TypeTraining type) {

        List<Training> trainings = trainingService.searchByTitleLevelAndType(keyword, level, type);
        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée avec le mot-clé '" + keyword +
                    "', le niveau '" + level + "' et le type '" + type + "'", HttpStatus.NOT_FOUND);
        }

        ByteArrayInputStream bis = trainingService.exportTrainingsToPDF(trainings);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=filtered-trainings-advanced.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/report")
    public ResponseEntity<String> getTrainingReport() {
        String report = trainingService.getTrainingReport();
        return new ResponseEntity<>(report, HttpStatus.OK);
    }


    @GetMapping(value = "/report/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadTrainingSummaryPDF() {
        ByteArrayInputStream bis = trainingService.generateTrainingSummaryPDF();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=training-summary-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/between")
    public ResponseEntity<?> getTrainingsBetweenDates(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<Training> trainings = trainingService.getTrainingsBetweenDates(start, end);

        if (trainings.isEmpty()) {
            return new ResponseEntity<>("Aucune formation trouvée entre " + start + " et " + end, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }


    @GetMapping("/report/monthly")
    public ResponseEntity<String> getMonthlyTrainingReport(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String report = trainingService.getMonthlyTrainingReport(start, end);

        return new ResponseEntity<>(report, HttpStatus.OK);
    }


    @GetMapping(value = "/report/monthly/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadMonthlyTrainingReportPDF(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        ByteArrayInputStream bis = trainingService.generateMonthlyReportPDF(start, end);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=monthly-training-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @DeleteMapping("/delete-past")
    public ResponseEntity<String> deletePastTrainings() {
        String message = trainingService.deletePastTrainings();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


}
