/*
 * This file is part of TradutorGalego.

 * TradutorGalego is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * TradutorGalego is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with TradutorGalego.  If not, see <http://www.gnu.org/licenses/>.
 */
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
