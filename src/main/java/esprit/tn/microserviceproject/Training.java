package esprit.tn.microserviceproject;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Training {
    private static final long serialVersionUID=6;
    @Id
    @GeneratedValue
    private  int id;
    private String title;
    private String level;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TypeTraining typeTraining;

    public Training() {};
    public Training(String title, String level, String description, TypeTraining typeTraining, LocalDate startDate, LocalDate endDate) {
        this.title=title;
        this.level=level;
        this.description=description;
        this.typeTraining=typeTraining;
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public TypeTraining getTypeTraining() {
        return typeTraining;
    }

    public void setTypeTraining(TypeTraining typeTraining) {
        this.typeTraining = typeTraining;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() { return startDate;}

    public void setStartDate(LocalDate startDate) { this.startDate = startDate;}

    public LocalDate getEndDate() { return endDate;}

    public void setEndDate(LocalDate endDate) { this.endDate = endDate;}

}
