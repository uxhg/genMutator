package xyz.facta.jtools.genmutator.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"description"})
public class AdjectiveList {
    @JsonProperty("adjs")
    private List<String> adjectives;

    // Getter and possibly setter
    public List<String> getAdjectives() {
        return adjectives;
    }

    public void setAdjectives(List<String> adjectives) {
        this.adjectives = adjectives;
    }
}