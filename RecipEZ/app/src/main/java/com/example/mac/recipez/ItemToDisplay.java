package com.example.mac.recipez;

/**
 * Created by dexterfung on 6/6/2017.
 */

public class ItemToDisplay {

    private String labelText;
    private String dataText;
    private String messageText;

    private int labelColour;
    private int dataColour;
    private int messageColour;

    private int dataBackground;

    private String dataDrawable;

    protected ItemToDisplay(String labelText, String dataText, String messageText,
                            int labelColour, int dataColour, int messageColour,
                            int dataBackground, String dataDrawable) {
        this.labelText = labelText;
        this.dataText = dataText;
        this.messageText = messageText;
        this.labelColour = labelColour;
        this.dataColour = dataColour;
        this.messageColour = messageColour;
        this.dataBackground = dataBackground;
        this.dataDrawable = dataDrawable;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getLabelColour() {
        return labelColour;
    }

    public void setLabelColour(int labelColour) {
        this.labelColour = labelColour;
    }

    public int getDataColour() {
        return dataColour;
    }

    public void setDataColour(int dataColour) {
        this.dataColour = dataColour;
    }

    public int getMessageColour() {
        return messageColour;
    }

    public void setMessageColour(int messageColour) {
        this.messageColour = messageColour;
    }

    public int getDataBackground() {
        return dataBackground;
    }

    public void setDataBackground(int dataBackground) {
        this.dataBackground = dataBackground;
    }

    public String getDataDrawable() {
        return dataDrawable;
    }

    public void setDataDrawable(String dataDrawable) {
        this.dataDrawable = dataDrawable;
    }

    @Override
    public String toString() {
        return "ItemToDisplay{" +
                "labelText='" + labelText + '\'' +
                ", dataText='" + dataText + '\'' +
                ", messageText='" + messageText + '\'' +
                ", labelColour=" + labelColour +
                ", dataColour=" + dataColour +
                ", messageColour=" + messageColour +
                ", dataBackground=" + dataBackground +
                ", dataDrawable='" + dataDrawable + '\'' +
                '}';
    }
}
