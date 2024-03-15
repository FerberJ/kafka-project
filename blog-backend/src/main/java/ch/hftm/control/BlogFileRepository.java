package ch.hftm.control;

import ch.hftm.entity.BlogFile;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogFileRepository implements PanacheRepository<BlogFile> {
    
}
