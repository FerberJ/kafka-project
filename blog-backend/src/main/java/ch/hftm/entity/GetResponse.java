package ch.hftm.entity;

import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetResponse {
    private InputStream stream;
    private String contentType;
    private BlogFile blogFile;
}