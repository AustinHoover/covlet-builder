package io.austinhoover;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFrame;

import com.google.gson.Gson;

import io.austinhoover.model.Config;
import io.austinhoover.ui.ConfigPanel;

/**
 * Hello world!
 *
 */
public class App {

    /**
     * Main method
     */
    public static void main(String[] args){

        //read in config
        String configString;
        try {
            configString = Files.readString(new File("./config.json").toPath());
        } catch (IOException e) {
            throw new Error(e);
        }
        Config config = new Gson().fromJson(configString, Config.class);

        //error check the config
        App.errorCheckConfig(config);

        //construct ui
        JFrame frame = new JFrame();
        frame.add(new ConfigPanel(config));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }

    /**
     * Error checks the config
     * @param config The config
     */
    private static void errorCheckConfig(Config config){
        //keys that are reserved (can't be specified in config.json)
        String[] reservedKeys = new String[]{
            ConfigPanel.KEY_COMPANY_NAME,
            ConfigPanel.KEY_ROLE_TITLE,
            ConfigPanel.KEY_DATE,
        };
        for(String reservedKey : reservedKeys){
            if(config.getContentMap().containsKey(reservedKey)){
                throw new Error("Config contains reserved key " + reservedKey);
            }
        }
        //check other mandatory data
        if(config.getClosingPhrase() == null || config.getClosingPhrase().equals("")){
            throw new Error("Closing phrase is empty");
        }
        if(config.getName() == null || config.getName().equals("")){
            throw new Error("Name is empty");
        }
        if(config.getIntroParagraph() == null || config.getIntroParagraph().equals("")){
            throw new Error("Intro paragraph is empty");
        }
        if(config.getClosingPhrase() == null || config.getClosingPhrase().equals("")){
            throw new Error("Closing phrase is empty");
        }
    }
}
