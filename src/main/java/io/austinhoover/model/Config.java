package io.austinhoover.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Main config file model class
 */
public class Config {

    /**
     * Map of tag -> content to include with that tag
     */
    private Map<String,String> contentMap = new HashMap<String,String>();

    /**
     * The intro paragraph's content
     */
    private String introParagraph;

    /**
     * The name of the person to generate a cover letter for
     */
    private String name;

    /**
     * The closing phrase of the letter (eg "Sincerely,", "Thank you,", etc)
     */
    private String closingPhrase;

    /**
     * Gets the content map
     * @return The content map
     */
    public Map<String, String> getContentMap(){
        return contentMap;
    }

    /**
     * Gets the content of the intro paragraph
     * @return The content of the intro paragraph
     */
    public String getIntroParagraph(){
        return introParagraph;
    }

    /**
     * Gets the name of the person to generate the letter for
     * @return The name
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the closing phrase of the letter
     * @return The closing phrase
     */
    public String getClosingPhrase(){
        return closingPhrase;
    }

}
