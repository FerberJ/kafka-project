package ch.hftm.control.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface BlogDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public class NewBlogDto {
        private String title;
        private String content;
        private List<Integer> files;
    }
}
