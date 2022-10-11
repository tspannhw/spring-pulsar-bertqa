package dev.datainmotion.bertqa.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "messageText",
        "name"
})
public class RawText {

    @JsonProperty("messageText")
    private String messageText;

    @JsonProperty("Name")
    private String name;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RawText() {
        super();
    }

    public RawText(String messageText, String name) {
        super();
        this.messageText = messageText;
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RawText.class.getSimpleName() + "[", "]")
                .add("messageText='" + messageText + "'")
                .add("name='" + name + "'")
                .toString();
    }
}