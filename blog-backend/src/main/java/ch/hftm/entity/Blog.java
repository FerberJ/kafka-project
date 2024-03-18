package ch.hftm.entity;




import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Blog {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private boolean valid;

 
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="BLOG_FILE")
    private List<BlogFile> files;
   
    public void addBlogFile(BlogFile blogFile) {
        files.add(blogFile);
    }

    public Blog(String title, String content, List<BlogFile> files) {
        this.title = title;
        this.content = content;
        this.files = files;
        this.valid = false;
    }
}