package es.galapps.android.tradutorgalego.model;

import java.io.Serializable;

public class HtmlTranslation implements Serializable {

    private static final long serialVersionUID = 6811960876686465249L;

    private String original;
    private String translation;
    private String translationHtml;
    private boolean showDicionarioIcon;
    private boolean showConxuGalegoIcon;

    public HtmlTranslation(String original, String translation, String translationHtml,
            boolean showDicionarioIcon, boolean showConxuGalegoIcon) {

        this.original = original;
        this.translation = translation;
        this.translationHtml = translationHtml;
        this.showDicionarioIcon = showDicionarioIcon;
        this.showConxuGalegoIcon = showConxuGalegoIcon;
    }

    public String getOriginal() {

        return this.original;
    }

    public String getTranslation() {

        return this.translation;
    }
    
    public String getTranslationHtml() {

        return this.translationHtml;
    }

    public boolean isShowDicionarioIcon() {

        return showDicionarioIcon;
    }

    public boolean isShowConxuGalegoIcon() {

        return showConxuGalegoIcon;
    }

}
