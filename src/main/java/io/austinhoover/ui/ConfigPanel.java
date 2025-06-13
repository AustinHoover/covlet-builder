package io.austinhoover.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import io.austinhoover.model.Config;

/**
 * The main config panel
 */
public class ConfigPanel extends JPanel {

    /**
     * Number of rows by default
     *  - Company name
     *  - Role Title
     *  - Date checkbox
     *  - Submit button
     */
    private static final int DEFAULT_ROWS = 4;

    /**
     * Name of the company
     */
    public static final String KEY_COMPANY_NAME = "companyName";

    /**
     * Title of the job
     */
    public static final String KEY_ROLE_TITLE = "roleTitle";

    /**
     * Date of listing
     */
    public static final String KEY_DATE = "date";

    /**
     * Font size to generate with
     */
    private static final int FONT_SIZE = 12;

    /**
     * Offset of a newline
     */
    private static final int NEWLINE_OFFSET = -FONT_SIZE * 2;

    /**
     * Left margin of the document
     */
    private static final int MARGIN_LEFT = 50;

    /**
     * Amount to divide wrap calculation by
     */
    private static final int WRAP_MODIFIER = 5;

    /**
     * UI String for company name input
     */
    private static final String UI_STRING_COMPANY_NAME = "Company Name";

    /**
     * UI String for role title input
     */
    private static final String UI_STRING_ROLE_TITLE = "Role Title";

    /**
     * UI String for date checkbox
     */
    private static final String UI_STRING_DATE = "Include Date?";

    /**
     * UI String for save button
     */
    private static final String UI_STRING_SAVE_BUTTON = "Save";

    /**
     * Constructor
     * @param config The config
     */
    public ConfigPanel(Config config){

        //set the layout for the panel
        this.setLayout(new GridLayout(DEFAULT_ROWS + config.getContentMap().keySet().size(),0));

        //The data to use for constructing the cover letter
        Map<String,Object> data = new HashMap<String,Object>();

        //options for company/role data
        {
            JPanel subPanel = new JPanel();
            subPanel.add(new JLabel(ConfigPanel.UI_STRING_COMPANY_NAME));
            JTextField textField = new JTextField(ConfigPanel.UI_STRING_COMPANY_NAME);
            subPanel.add(textField);
            textField.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e){}
                public void keyPressed(KeyEvent e){}
                public void keyReleased(KeyEvent e) {
                    data.put(ConfigPanel.KEY_COMPANY_NAME, textField.getText());
                }
            });
            this.add(subPanel);
            data.put(ConfigPanel.KEY_COMPANY_NAME, ConfigPanel.UI_STRING_COMPANY_NAME);
        }
        {
            JPanel subPanel = new JPanel();
            subPanel.add(new JLabel(ConfigPanel.UI_STRING_ROLE_TITLE));
            JTextField textField = new JTextField(ConfigPanel.UI_STRING_ROLE_TITLE);
            subPanel.add(textField);
            textField.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e){}
                public void keyPressed(KeyEvent e){}
                public void keyReleased(KeyEvent e) {
                    data.put(ConfigPanel.KEY_ROLE_TITLE, textField.getText());
                }
            });
            this.add(subPanel);
            data.put(ConfigPanel.KEY_ROLE_TITLE, ConfigPanel.UI_STRING_ROLE_TITLE);
        }
        {
            JPanel subPanel = new JPanel();
            subPanel.add(new JLabel(ConfigPanel.UI_STRING_DATE));
            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
                data.put(ConfigPanel.KEY_DATE, checkBox.isSelected());
            }});
            checkBox.setSelected(true); //date is included by default
            subPanel.add(checkBox);
            this.add(subPanel);
            data.put(ConfigPanel.KEY_DATE, true);
        }

        //generate main options
        for(String key : config.getContentMap().keySet()){
            JPanel subPanel = new JPanel();
            subPanel.add(new JLabel(key));
            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
                data.put(key, checkBox.isSelected());
            }});
            subPanel.add(checkBox);
            this.add(subPanel);
        }

        //add save button
        JButton saveButton = new JButton(ConfigPanel.UI_STRING_SAVE_BUTTON);
        saveButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
            generate(config,data);
        }});
        this.add(saveButton);
    }

    /**
     * Generates the cover letter pdf
     */
    private void generate(Config config, Map<String,Object> data){
        //current vertical offset into the document
        try(PDDocument document = new PDDocument()){
            //add page
            PDPage page = new PDPage();
            document.addPage(page);
            PDRectangle bounds = page.getMediaBox();

            float totalWidth = bounds.getWidth();
            int wrapSize = (int)((totalWidth - (MARGIN_LEFT * 2)) / WRAP_MODIFIER);

            //add text
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)){
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), FONT_SIZE);
                contentStream.newLineAtOffset(MARGIN_LEFT, bounds.getHeight() - MARGIN_LEFT);

                //date
                {
                    if((Boolean)data.get(ConfigPanel.KEY_DATE) == true){
                        contentStream.newLineAtOffset(0,-FONT_SIZE);
                        String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
                        ConfigPanel.writeParagraph(contentStream, dateString);
                    }
                }

                //intro paragraph
                {
                    contentStream.newLineAtOffset(0,-FONT_SIZE);
                    String introParaText = config.getIntroParagraph()
                        .replace(KEY_COMPANY_NAME, (String)data.get(KEY_COMPANY_NAME))
                        .replace(KEY_ROLE_TITLE, (String)data.get(KEY_ROLE_TITLE));
                    String introParaWrapped = WordUtils.wrap(introParaText, wrapSize, "\n", true).replace("\t", "        ");
                    ConfigPanel.writeParagraph(contentStream, introParaWrapped);
                }

                //for each checked box
                for(String key : config.getContentMap().keySet()){
                    if(data.containsKey(key) && (Boolean)data.get(key) == true){
                        contentStream.newLineAtOffset(0,-FONT_SIZE);
                        String keyPara = config.getContentMap().get(key);
                        String keyParaWrapped = WordUtils.wrap(keyPara, wrapSize, "\n", true).replace("\t", "        ");
                        ConfigPanel.writeParagraph(contentStream, keyParaWrapped);
                    }
                }

                //closing of letter
                contentStream.newLineAtOffset(0,NEWLINE_OFFSET);
                contentStream.showText(config.getClosingPhrase());

                //name at bottom
                contentStream.newLineAtOffset(0,NEWLINE_OFFSET);
                contentStream.showText(config.getName());

                contentStream.endText();
            }

            //save doc
            document.save("./covlet.pdf");
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /**
     * Writes a paragraph to a content stream
     * @param contentStream The content stream
     * @param text The paragraph text (where new lines are delineated with '\n')
     * @throws IOException Thrown by PDFBox methods
     */
    private static void writeParagraph(PDPageContentStream contentStream, String text) throws IOException {
        String[] lines = text.split("\n");
        for(String line : lines){
            contentStream.newLineAtOffset(0,-FONT_SIZE);
            contentStream.showText(line);
        }
    }

}
