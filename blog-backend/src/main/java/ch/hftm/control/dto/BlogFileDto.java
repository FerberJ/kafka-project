package ch.hftm.control.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface BlogFileDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public class NewBlogFileDto {
        private String filename;
        private String bucket;
    }
}
