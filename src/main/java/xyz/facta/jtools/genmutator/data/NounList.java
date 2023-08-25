package xyz.facta.jtools.genmutator.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"description"})
public class NounList {
    @JsonProperty("nouns")
    private List<String> nouns;

    // Getter and possibly setter
    public List<String> getNouns() {
        return nouns;
    }
}