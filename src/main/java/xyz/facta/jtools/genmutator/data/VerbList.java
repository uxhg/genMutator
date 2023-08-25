package xyz.facta.jtools.genmutator.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"description"})
public class VerbList {
    @JsonProperty("verbs")
    private List<Verb> verbs;

    // Getter and possibly setter
    public List<Verb> getVerbs() {
        return verbs;
    }

    public void setVerbs(List<Verb> verbs) {
        this.verbs = verbs;
    }


    public static class Verb {
        private String present;
        private String past;

        public String getPresent() {
            return present;
        }
        public String getPast() {
            return past;
        }
    }

}
