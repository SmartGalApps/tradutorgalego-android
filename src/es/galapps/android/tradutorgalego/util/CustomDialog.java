package es.galapps.android.tradutorgalego.util;

import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import es.galapps.android.tradutorgalego.R;

public class CustomDialog {

    public static AlertDialog create(Context context, int titleId,
            int messageId, int drawableId) {

        final TextView message = new TextView(context);
        // i.e.: R.string.dialog_message =>
        // "Test this dialog following the link to dtmilano.blogspot.com"
        final SpannableString s = new SpannableString(
                context.getText(messageId));
        Linkify.addLinks(s, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        return new AlertDialog.Builder(context).setTitle(titleId)
                .setCancelable(true).setIcon(drawableId)
                .setPositiveButton(R.string.ok, null).setView(message).create();
    }
}