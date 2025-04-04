package esprit.tn.microserviceproject;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {

    List<Training> findByLevel(String level);

    List<Training> findByTypeTraining(TypeTraining typeTraining);

    List<Training> findByTitleContainingIgnoreCase(String keyword);

    List<Training> findByLevelAndTypeTraining(String level, TypeTraining typeTraining);

    List<Training> findByTitleContainingIgnoreCaseAndLevel(String keyword, String level);

    List<Training> findByTitleContainingIgnoreCaseAndTypeTraining(String keyword, TypeTraining typeTraining);

    List<Training> findByTitleContainingIgnoreCaseAndLevelAndTypeTraining(String keyword, String level, TypeTraining typeTraining);


    @Query("SELECT t.typeTraining, COUNT(t) FROM Training t GROUP BY t.typeTraining")
    List<Object[]> countTrainingsByType();

    @Query("SELECT t.level, COUNT(t) FROM Training t GROUP BY t.level")
    List<Object[]> countTrainingsByLevel();

    List<Training> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    void deleteByEndDateBefore(LocalDate date);



}
