package ch.hftm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Files")
public class BlogFile {
    @Id
    @GeneratedValue
    private Long id;
    private String filename;
    private String bucket;

    public BlogFile(String filename, String bucket) {
        this.filename = filename;
        this.bucket = bucket;
    }
}
