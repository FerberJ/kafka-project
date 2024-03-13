package ch.hftm.entity;




import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    @OneToOne(cascade = CascadeType.ALL)
    private List<BlogFile> files;

    public Blog(String title, String content) {
        this.title = title;
        this.content = content;
        this.valid = false;
    }
}