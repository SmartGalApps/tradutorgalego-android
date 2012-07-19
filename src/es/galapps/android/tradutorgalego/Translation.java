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
package es.galapps.android.tradutorgalego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import es.galapps.android.tradutorgalego.model.HtmlTranslation;
import es.galapps.android.tradutorgalego.util.AboutDialog;
import es.galapps.android.tradutorgalego.util.CustomSlidingDrawer;
import es.galapps.android.tradutorgalego.util.HelpDialog;
import es.galapps.android.tradutorgalego.util.InstalledApplicationsUtils;
import es.galapps.android.tradutorgalego.util.TranslateTask;

public class Translation extends Activity {

	private static final int CONJUGATE_REQUEST_CODE = 1;
	private static final int DEFINE_REQUEST_CODE = 2;

	protected Typeface boldFont;
	private WebView translation;
	protected TextView defineText;
	private ImageButton defineButton;
	protected TextView conjugateText;
	private ImageButton conjugateButton;
	private CustomSlidingDrawer drawer;

	protected String original;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.translation);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		this.boldFont = Typeface.createFromAsset(getAssets(), "fonts/CantarellBold.ttf");

		this.defineButton = (ImageButton) this.findViewById(R.id.define);
		this.defineText = (TextView) this.findViewById(R.id.defineText);
		this.conjugateButton = (ImageButton) this.findViewById(R.id.conjugate);
		this.conjugateText = (TextView) this.findViewById(R.id.conjugateText);

		this.translation = (WebView) findViewById(R.id.translation);
		this.translation.setInitialScale(100);
		this.translation.getSettings().setBuiltInZoomControls(false);
		this.translation.setBackgroundColor(0);

		ViewGroup zoom = (ViewGroup) findViewById(R.id.zoom);
		zoom.addView(this.translation.getZoomControls());

		Bundle extras = getIntent().getExtras();

		if (extras.containsKey("translation")) {
			final HtmlTranslation htmlTranslation = (HtmlTranslation) extras
					.getSerializable("translation");
			this.original = htmlTranslation.getOriginal();
			this.printResult(htmlTranslation.getTranslationHtml());

			if (htmlTranslation.isShowDicionarioIcon() || htmlTranslation.isShowConxuGalegoIcon()) {

				final ImageView handle = (ImageView) this.findViewById(R.id.handle);
				this.drawer = (CustomSlidingDrawer) this.findViewById(R.id.drawer);
				this.drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

					@Override
					public void onDrawerOpened() {

						handle.setImageResource(R.drawable.handle_on);
					}
				});
				this.drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

					@Override
					public void onDrawerClosed() {

						handle.setImageResource(R.drawable.handle_off);
					}
				});

				if (htmlTranslation.isShowDicionarioIcon()) {

					this.defineText.setText(getString(R.string.defineWith,
							htmlTranslation.getTranslation()));

					this.defineButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {

							if (InstalledApplicationsUtils
									.isDiccionarioGalegoInstalled(Translation.this)) {
								Intent defineIntent = new Intent(Intent.ACTION_VIEW);
								defineIntent.setComponent(new ComponentName(
										"es.galapps.android.diccionariogalego",
										"es.galapps.android.diccionariogalego.Definitions"));
								defineIntent.putExtra("word", htmlTranslation.getTranslation());
								startActivityForResult(defineIntent, DEFINE_REQUEST_CODE);
							} else {
								new AlertDialog.Builder(Translation.this)
										.setTitle(R.string.downloadDicionarioGalego)
										.setMessage(R.string.downloadDicionarioGalegoMessage)
										.setCancelable(true)
										.setPositiveButton(
												Translation.this.getString(android.R.string.ok),
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface arg0,
															int arg1) {

														Intent goToMarket = new Intent(
																Intent.ACTION_VIEW,
																Uri.parse("market://details?id=es.galapps.android.diccionariogalego"));
														startActivity(goToMarket);
													}

												})
										.setNegativeButton(
												Translation.this.getString(R.string.cancel), null)
										.create().show();
							}
						}

					});

				} else {
					this.defineText.setVisibility(View.GONE);
					this.defineButton.setVisibility(View.GONE);
				}

				if (htmlTranslation.isShowConxuGalegoIcon()) {

					this.conjugateText.setText(getString(R.string.conjugateWith,
							htmlTranslation.getTranslation()));

					this.conjugateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {

							if (InstalledApplicationsUtils.isConxuGalegoInstalled(Translation.this)) {
								Intent conjugateIntent = new Intent(Intent.ACTION_VIEW);
								conjugateIntent.setComponent(new ComponentName(
										"es.sonxurxo.android.conxugalego",
										"es.sonxurxo.android.conxugalego.Verbs"));
								conjugateIntent.putExtra("infinitive",
										htmlTranslation.getTranslation());
								startActivity(conjugateIntent);
							} else {
								new AlertDialog.Builder(Translation.this)
										.setTitle(R.string.downloadConxuGalego)
										.setMessage(R.string.downloadConxuGalegoMessage)
										.setCancelable(true)
										.setPositiveButton(
												Translation.this.getString(android.R.string.ok),
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface arg0,
															int arg1) {

														Intent goToMarket = new Intent(
																Intent.ACTION_VIEW,
																Uri.parse("market://details?id=es.sonxurxo.android.conxugalego"));
														startActivity(goToMarket);
													}

												})
										.setNegativeButton(
												Translation.this.getString(R.string.cancel), null)
										.create().show();
							}
						}

					});

				} else {
					this.conjugateText.setVisibility(View.GONE);
					this.conjugateButton.setVisibility(View.GONE);
				}

			} else {
				this.findViewById(R.id.drawer).setVisibility(View.GONE);
			}
		} else if (extras.containsKey("word")) {
			this.findViewById(R.id.drawer).setVisibility(View.GONE);
			this.original = extras.getString("word");
			this.search(this.original);
		}

		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
	}

	protected void search(final String theWord) {

		new TranslateTask(Translation.this) {

			@Override
			protected void onPostExecuteTranslation(HtmlTranslation result) {

				Translation.this.printResult(result.getTranslationHtml());
			}

			@Override
			protected void onPostExecuteConnectionError() {

				Intent data = new Intent();
				setResult(-2, data);
				finish();
			}

		}.execute(theWord);
	}

	protected void printResult(String theDefinition) {

		String def = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\" />";
		if (this.original.length() < 50) {
			def += "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles_few_words.css\" />";
		}
		def += "</head><body>" + theDefinition + "</body></html>";
		this.translation.loadDataWithBaseURL("file:///android_asset/", def, "text/html",
				"ISO-8859-1", "");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.translation_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.about:
			AlertDialog builder;
			try {
				builder = AboutDialog.create(this);
				builder.show();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		case R.id.share:
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					this.getString(R.string.shareTitle));
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					this.getString(R.string.webURL, this.original));

			startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
			return true;
		case R.id.help:
			AlertDialog helpDialog;

			helpDialog = HelpDialog.create(this);
			helpDialog.show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case CONJUGATE_REQUEST_CODE:
			switch (resultCode) {
			case -1:
				Toast.makeText(this,
						getString(R.string.conjugateNotFound, data.getStringExtra("word")),
						Toast.LENGTH_SHORT).show();
				break;
			case -2:
				Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
				break;
			}
			break;
		case DEFINE_REQUEST_CODE:
			switch (resultCode) {
			case -1:
				Toast.makeText(this,
						getString(R.string.wordNotFound, data.getStringExtra("infinitive")),
						Toast.LENGTH_SHORT).show();
				break;
			case -2:
				Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT)
						.show();
				break;
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {

		if (this.drawer != null && this.drawer.isOpened()) {

			this.drawer.close();

		} else {

			super.onBackPressed();
		}
	}
}
