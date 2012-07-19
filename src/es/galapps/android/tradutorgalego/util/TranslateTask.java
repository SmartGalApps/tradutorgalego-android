package es.galapps.android.tradutorgalego.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;
import es.galapps.android.tradutorgalego.R;
import es.galapps.android.tradutorgalego.model.HtmlTranslation;

public abstract class TranslateTask extends
        AsyncTask<String, Void, HtmlTranslation> {

    protected static final String SERVER_URL = "http://www.xunta.es/tradutor/text.do";
    protected static final String OPTIONS = "OPTIONS:";
    protected static final String CONNECTION_ERROR = "CONNECTION_ERROR";

    private final ProgressDialog dialog;
    private final Context context;

    public TranslateTask(Context context) {

        super();
        this.context = context;
        this.dialog = new ProgressDialog(context);
        this.dialog.setCancelable(true);
        this.dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {

                TranslateTask.this.cancel(true);
            }

        });
        this.dialog.setMessage(this.context.getString(R.string.loadingData));
    }

    @Override
    protected void onPreExecute() {

        this.dialog.show();
    }

    private String getLanguageCode(String language) {

        if (language.equals("GALICIAN")) {
            return "gl";
        }
        if (language.equals("SPANISH")) {
            return "es";
        }
        if (language.equals("ENGLISH")) {
            return "en";
        }
        if (language.equals("CATALAN")) {
            return "cat";
        }
        return "fr";
    }

    private String getLanguage(String languageCode) {

        if (languageCode.equals("gl")) {
            return "GALICIAN";
        }
        if (languageCode.equals("es")) {
            return "SPANISH";
        }
        if (languageCode.equals("en")) {
            return "ENGLISH";
        }
        if (languageCode.equals("cat")) {
            return "CATALAN";
        }
        return "FRENCH";
    }

    private String getTranslationDirection(String origin, String dest) {

        return this.getLanguage(origin) + "-" + this.getLanguage(dest);
    }

    @Override
    protected HtmlTranslation doInBackground(String... parameters) {

        try {
            String translationDirection = "GALICIAN-SPANISH";
            if (parameters.length != 1) {
                translationDirection = this.getTranslationDirection(
                        parameters[1], parameters[2]);
            }
            String pivotLanguage = "";
            if (translationDirection.contains("FRENCH")
                    || translationDirection.contains("CATALAN")) {
                pivotLanguage = "&DTS_PIVOT_LANGUAGE="
                        + URLEncoder.encode("SPANISH", "UTF-8");
            }
            URL url;
            String data = "text=" + URLEncoder.encode(parameters[0], "UTF-8");
            data += "&translationDirection="
                    + URLEncoder.encode(translationDirection, "UTF-8")
                    + "&subjectArea=" + URLEncoder.encode("(GV)", "UTF-8")
                    + pivotLanguage;

            url = new URL(SERVER_URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            Document doc = Jsoup.parse(sb.toString());

            Element translationElement = doc.select("IFRAME").first();

            String translation = translationElement.text();
            String translationHtml = translationElement.html();

            translationHtml = translationHtml.replaceAll("&amp;lt;", "<");
            translationHtml = translationHtml.replaceAll("&amp;gt;", ">");
            translationHtml = translationHtml.replaceAll("&iquest;", "");
            translationHtml = translationHtml.replaceAll("&Acirc;&iexcl;", "¡");
            translationHtml = translationHtml.replaceAll("class=unknown",
                    "class=\"unknown\"");
            translationHtml = this.getPreTranslation(this
                    .getLanguageCode(translationDirection.split("-")[1]))
                    + translationHtml
                    + this.getPostTranslation()
                    + this.getPreOriginal(this
                            .getLanguageCode(translationDirection.split("-")[0]))
                    + encode(parameters[0]) + this.getPostOriginal();

            boolean showDicionarioIcon = false;
            boolean showConxugalegoIcon = false;

            if (parameters.length == 1 || parameters[2].equals("gl")) {

                showDicionarioIcon = parameters[0].split(" ").length == 1;
                showConxugalegoIcon = VolgaChecker.exists(context, translation);

            }

            return new HtmlTranslation(decode(parameters[0]), translation,
                    encode(translationHtml), showDicionarioIcon,
                    showConxugalegoIcon);

        } catch (IOException e) {
            e.printStackTrace();
            return new HtmlTranslation(decode(parameters[0]), CONNECTION_ERROR,
                    CONNECTION_ERROR, false, false);
        }

    }

    private String getPreTranslation(String language) {

        String translationDrawableName = ResourceUtils.getDrawableName(
                language, true);
        return "<div class=\"translationHeader\"><div class=\"translationTitle\">"
                + this.context.getString(R.string.translatedText)
                + "</div><div class=\"translationImage\"><img src=\"file:///android_res/drawable/"
                + translationDrawableName
                + ".png\" ></div></div><div class=\"translation\">";
    }

    private String getPostTranslation() {

        return "</div>";
    }

    private String getPreOriginal(String language) {

        String translationDrawableName = ResourceUtils.getDrawableName(
                language, true);
        return "<div class=\"originalHeader\"><div class=\"originalTitle\">"
                + this.context.getString(R.string.originalText)
                + "</div><div class=\"originalImage\"><img src=\"file:///android_res/drawable/"
                + translationDrawableName
                + ".png\" ></div></div><div class=\"original\">";
    }

    private String getPostOriginal() {

        return "</div>";
    }

    protected boolean isNumber(String s) {

        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(HtmlTranslation result) {

        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }

        if (result.getTranslationHtml().equals(CONNECTION_ERROR)) {
            this.onPostExecuteConnectionError();
        } else {
            this.onPostExecuteTranslation(result);
        }
    }

    protected abstract void onPostExecuteConnectionError();

    protected abstract void onPostExecuteTranslation(HtmlTranslation result);

    @Override
    protected void onCancelled() {

        super.onCancelled();
        Toast.makeText(this.context, R.string.cancelled, Toast.LENGTH_SHORT)
                .show();
    }

    private String decode(String word) {

        return word.replace("&aacute;", "á").replace("&eacute;", "é")
                .replace("&iacute;", "í").replace("&oacute;", "ó")
                .replace("&uacute;", "ú").replace("&Uuml;", "ü")
                .replace("&ntilde;", "ñ");
    }

    private String encode(String word) {

        return word.replace("á", "&aacute;").replace("é", "&eacute;")
                .replace("í", "&iacute;").replace("ó", "&oacute;")
                .replace("ú", "&uacute;").replace("ü", "&Uuml;")
                .replace("ñ", "&ntilde;").replace("&amp;amp;", "&")
                .replace("%20", " ").replace("\n", "<br/>").replace("%3F", "?")
                .replace("¡", "&#161;");
    }

}
