package ch.hftm.control.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface BlogFileDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class NewBlogFileDto {
        private String filename;
        private String bucket;
        private String hashcode;
        private String displayname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UpdateBlogFileDto {
        private String displayname;
    }
}
