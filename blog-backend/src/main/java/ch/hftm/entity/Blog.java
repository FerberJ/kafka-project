package ch.hftm.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Blog {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private boolean valid;

    public Blog(String title, String content) {
        this.title = title;
        this.content = content;
        this.valid = false;
    }
}