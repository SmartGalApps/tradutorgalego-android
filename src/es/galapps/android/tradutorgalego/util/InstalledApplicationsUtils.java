package es.galapps.android.tradutorgalego.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class InstalledApplicationsUtils {

	public static boolean isConxuGalegoInstalled(Context context) {

		Intent intent = new Intent();
		intent.setComponent(new ComponentName("es.sonxurxo.android.conxugalego",
				"es.sonxurxo.android.conxugalego.Verbs"));
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static boolean isDiccionarioGalegoInstalled(Context context) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("es.galapps.android.diccionariogalego",
				"es.galapps.android.diccionariogalego.Definitions"));
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}
