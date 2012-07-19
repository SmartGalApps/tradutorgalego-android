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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import es.galapps.android.tradutorgalego.model.HtmlTranslation;
import es.galapps.android.tradutorgalego.util.AboutDialog;
import es.galapps.android.tradutorgalego.util.HelpDialog;
import es.galapps.android.tradutorgalego.util.ResourceUtils;
import es.galapps.android.tradutorgalego.util.TranslateTask;

public class TradutorGalegoActivity extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private static final String VERSION = "VERSION:";
	protected static final int[] LANGUAGES_RESOURCES_IDS = {
			R.string.es, R.string.cat, R.string.en, R.string.fr
	};
	protected static final String[] LANGUAGES_CODES = {
			"es", "cat", "en", "fr"
	};

	protected EditText mText;
	protected ImageButton mLaunchButton;

	private Button mLanguageFrom;
	private Button mLanguageTo;
	private ImageButton mChangeLanguageDirection;

	protected String[] translationDirection;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (prefs.getInt(VERSION, 1) < getPackageVersion()) {
			showNews(getLayoutInflater(), this);
		}

		this.mLanguageFrom = (Button) findViewById(R.id.languageFrom);
		this.mLanguageFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final LanguageListAdapter adapter = new LanguageListAdapter(
						TradutorGalegoActivity.this, R.layout.language_list_item, LANGUAGES_CODES);
				AlertDialog.Builder builder = new AlertDialog.Builder(TradutorGalegoActivity.this);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {

						TradutorGalegoActivity.this.setTranslationDirection(new String[] {
								adapter.getItem(item),
								TradutorGalegoActivity.this.translationDirection[1]
						});
						TradutorGalegoActivity.this.toastTranslationDirection();
					}
				});
				builder.setTitle(R.string.translateFrom);
				builder.create().show();
			}

		});
		this.mLanguageTo = (Button) findViewById(R.id.languageTo);
		this.mLanguageTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final LanguageListAdapter adapter = new LanguageListAdapter(
						TradutorGalegoActivity.this, R.layout.language_list_item, LANGUAGES_CODES);
				AlertDialog.Builder builder = new AlertDialog.Builder(TradutorGalegoActivity.this);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {

						TradutorGalegoActivity.this.setTranslationDirection(new String[] {
								TradutorGalegoActivity.this.translationDirection[0],
								adapter.getItem(item)
						});
						TradutorGalegoActivity.this.toastTranslationDirection();
					}
				});
				builder.setTitle(R.string.translateTo);
				builder.create().show();
			}

		});
		this.mChangeLanguageDirection = (ImageButton) findViewById(R.id.changeLanguageDirection);
		this.mChangeLanguageDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				TradutorGalegoActivity.this.setTranslationDirection(new String[] {
						TradutorGalegoActivity.this.translationDirection[1],
						TradutorGalegoActivity.this.translationDirection[0]
				});
				TradutorGalegoActivity.this.toastTranslationDirection();
			}

		});

		this.mText = (EditText) findViewById(R.id.input);
		final Drawable x = getResources().getDrawable(R.drawable.presence_offline);
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		this.mText.setCompoundDrawables(null, null, null, null);
		this.mText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (TradutorGalegoActivity.this.mText.getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				if (event.getX() > TradutorGalegoActivity.this.mText.getWidth()
						- TradutorGalegoActivity.this.mText.getPaddingRight()
						- x.getIntrinsicWidth()) {
					TradutorGalegoActivity.this.mText.setText("");
					TradutorGalegoActivity.this.mText.setCompoundDrawables(null, null, null, null);
				}
				return false;
			}
		});

		this.mText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				// Empty on purpose
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				// Empty on purpose
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				TradutorGalegoActivity.this.mText.setCompoundDrawables(null, null,
						TradutorGalegoActivity.this.mText.getText().toString().equals("") ? null
								: x, null);
				if (TradutorGalegoActivity.this.mText.length() == 0) {
					TradutorGalegoActivity.this.mLaunchButton.setImageResource(R.drawable.micro2);
					TradutorGalegoActivity.this.mLaunchButton
							.setOnClickListener(new SpeakRecognitionClickListener());
				} else {
					TradutorGalegoActivity.this.mLaunchButton
							.setImageResource(R.drawable.flechaenvio);
					TradutorGalegoActivity.this.mLaunchButton
							.setOnClickListener(new TranslateClickListener());
				}
			}
		});
		this.mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					TradutorGalegoActivity.this.search(TradutorGalegoActivity.this.mText.getText()
							.toString());
					return true;
				}
				return false;
			}
		});

		this.mLaunchButton = (ImageButton) findViewById(R.id.search);

		if (!prefs.contains("profile")) {
			ProfileDialog profileDialog = new ProfileDialog(this);
			profileDialog.setCancelable(false);
			profileDialog.show();
		} else {
			String profile = prefs.getString("profile", "galego");
			if (profile.equals("galego")) {
				setGalicianLocale();
				this.setTranslationDirection(new String[] {
						"gl", "es"
				});
			} else {
				setDefaultLocale();
				Locale defaultLocale = Locale.getDefault();
				if (defaultLocale.getLanguage().equals("en")) {
					this.setTranslationDirection(new String[] {
							"en", "gl"
					});
				} else if (defaultLocale.getLanguage().equals("fr")) {
					this.setTranslationDirection(new String[] {
							"fr", "gl"
					});
				} else if (defaultLocale.getLanguage().equals("es") ||
						defaultLocale.getLanguage().equals("gl")) {
					this.setTranslationDirection(new String[] {
							"es", "gl"
					});
				} else if (defaultLocale.getLanguage().equals("cat")) {
					this.setTranslationDirection(new String[] {
							"cat", "gl"
					});
				} else {
					this.setTranslationDirection(new String[] {
							"en", "gl"
					});
				}
			}
		}

		if (this.hasSpeakRecognition()) {
			this.mLaunchButton.setOnClickListener(new SpeakRecognitionClickListener());
		} else {
			this.mLaunchButton.setOnClickListener(new TranslateClickListener());
		}

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("word")) {
			String word = getIntent().getExtras().getString("word");
			this.mText.setText(word);
			this.setTranslationDirection(new String[] {
					"gl", "es"
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		this.mLanguageFrom.setText(ResourceUtils
				.getLanguageName(this, this.translationDirection[0]));
		this.mLanguageFrom.setText(ResourceUtils
				.getLanguageName(this, this.translationDirection[1]));
		super.onConfigurationChanged(newConfig);
	}

	protected void setGalicianLocale() {

		Configuration config = new Configuration();
		config.locale = new Locale("gl");
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	protected void setDefaultLocale() {

		Configuration config = new Configuration();
		config.locale = Locale.getDefault();
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	class SpeakRecognitionClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage()
					.getName());
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}
	}

	class TranslateClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			TradutorGalegoActivity.this.search(TradutorGalegoActivity.this.mText.getText()
					.toString());
		}
	}

	protected boolean hasSpeakRecognition() {

		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		return activities.size() != 0;
	}

	protected void toastTranslationDirection() {

		Toast.makeText(
				getApplicationContext(),
				ResourceUtils.getLanguageName(TradutorGalegoActivity.this,
						TradutorGalegoActivity.this.translationDirection[0])
						+ " >> "
						+ ResourceUtils.getLanguageName(TradutorGalegoActivity.this,
								TradutorGalegoActivity.this.translationDirection[1]),
				Toast.LENGTH_SHORT).show();
	}

	public void setTranslationDirection(String[] direction) {

		this.translationDirection = direction;
		this.mLanguageFrom.setText(ResourceUtils
				.getLanguageName(this, this.translationDirection[0]));
		this.mLanguageTo.setText(ResourceUtils.getLanguageName(this, this.translationDirection[1]));
		if (direction[0].equals("gl")) {
			this.mLanguageFrom.setClickable(false);
			this.mLanguageFrom.setTextColor(Color.WHITE);
			this.mLanguageFrom.setBackgroundResource(R.drawable.botonfondogalicia);
			this.mLanguageFrom.setCompoundDrawablesWithIntrinsicBounds(
					ResourceUtils.getDrawableResourceId(this.translationDirection[0], true), 0, 0,
					0);
			this.mLanguageTo.setBackgroundResource(R.drawable.button);
			this.mLanguageTo.setClickable(true);
			this.mLanguageTo.setTextColor(Color.parseColor("#000099"));
			this.mLanguageTo.setCompoundDrawablesWithIntrinsicBounds(
					ResourceUtils.getDrawableResourceId(this.translationDirection[1], true), 0,
					R.drawable.flecha, 0);
		} else {
			this.mLanguageFrom.setClickable(true);
			this.mLanguageFrom.setTextColor(Color.parseColor("#000099"));
			this.mLanguageFrom.setBackgroundResource(R.drawable.button);
			this.mLanguageFrom.setCompoundDrawablesWithIntrinsicBounds(
					ResourceUtils.getDrawableResourceId(this.translationDirection[0], true), 0,
					R.drawable.flecha, 0);
			this.mLanguageTo.setBackgroundResource(R.drawable.botonfondogalicia);
			this.mLanguageTo.setClickable(false);
			this.mLanguageTo.setTextColor(Color.WHITE);
			this.mLanguageTo.setCompoundDrawablesWithIntrinsicBounds(
					ResourceUtils.getDrawableResourceId(this.translationDirection[1], true), 0, 0,
					0);
		}
	}

	protected void search(final String text) {

		if (!text.equals("")) {
			if (!haveInternet()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(TradutorGalegoActivity.this);
				builder.setTitle(R.string.noDataConnection);
				builder.setMessage(R.string.noDataConnectionMessage);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.configureNetwork,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dlalog, int arg1) {

								startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
							}

						});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dlalog, int arg1) {

						Toast.makeText(TradutorGalegoActivity.this, R.string.noDataDecided,
								Toast.LENGTH_SHORT).show();
					}

				});
				builder.create().show();
			} else {
				new TranslateTask(TradutorGalegoActivity.this) {

					@Override
					protected void onPostExecuteTranslation(HtmlTranslation result) {

						Intent translationIntent = new Intent(getApplicationContext(),
								Translation.class);
						translationIntent.putExtra("translation", result);
						translationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(translationIntent);
					}

					@Override
					protected void onPostExecuteConnectionError() {

						Toast.makeText(TradutorGalegoActivity.this,
								getString(R.string.connectionError), Toast.LENGTH_LONG).show();
					}
				}.execute(text, TradutorGalegoActivity.this.translationDirection[0],
						TradutorGalegoActivity.this.translationDirection[1]);
			}
		}
	}

	protected boolean haveInternet() {

		NetworkInfo info = ((ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info == null || !info.isConnectedOrConnecting()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.profile:
			new ProfileDialog(this).show();
			return true;
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
		case R.id.help:
			AlertDialog helpDialog;

			helpDialog = HelpDialog.create(this);
			helpDialog.show();
			return true;
		case R.id.share:
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					this.getString(R.string.shareTitle));
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					this.getString(R.string.galappsURL));

			startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {

		super.onPause();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor edit = prefs.edit();
		edit.putInt(VERSION, getPackageVersion());
		edit.commit();

	}

	private final int getPackageVersion() {

		try {

			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);

			return pinfo.versionCode;

		} catch (NameNotFoundException e) {
			// Empty on purpose
		}

		return 0;

	}

	private final void showNews(LayoutInflater inflater, Context context) {

		View messageView = inflater.inflate(R.layout.news, null, false);

		TextView textView = (TextView) messageView.findViewById(R.id.news_title);
		textView.setTextColor(Color.WHITE);

		TextView textView2 = (TextView) messageView.findViewById(R.id.news_text);
		textView2.setTextColor(Color.WHITE);
		
		Linkify.addLinks(textView2, Linkify.ALL);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.app_name);
		builder.setView(messageView);
		builder.setPositiveButton(R.string.ok, null);
		builder.create();
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			this.mText.setText(matches.get(0));
			// mList.setAdapter(new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1,
			// matches));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	class ProfileDialog extends Dialog {

		public ProfileDialog(final Context context) {

			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.profile_dialog);
			final ImageView sonGalegoIcon = ((ImageView) findViewById(R.id.sonGalego));
			sonGalegoIcon
					.setImageDrawable(context.getResources().getDrawable(R.drawable.songalego));
			final ImageView sonDeForaIcon = ((ImageView) findViewById(R.id.sonDeFora));
			sonDeForaIcon
					.setImageDrawable(context.getResources().getDrawable(R.drawable.sondefora));

			((RelativeLayout) findViewById(R.id.sonGalegoLayout))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							ProfileDialog.this.setProfile("galego");
							TradutorGalegoActivity.this.setTranslationDirection(new String[] {
									"gl", "es"
							});
							ProfileDialog.this.cancel();
						}

					});
			((RelativeLayout) findViewById(R.id.sonDeForaLayout))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							sonDeForaIcon.setImageDrawable(context.getResources().getDrawable(
									R.drawable.sondefora));
							ProfileDialog.this.setProfile("defora");
							TradutorGalegoActivity.this.setTranslationDirection(new String[] {
									"es", "gl"
							});
							ProfileDialog.this.cancel();
						}

					});
		}

		protected void setProfile(String profile) {

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext()
					.getApplicationContext());
			Editor edit = prefs.edit();
			edit.putString("profile", profile);
			edit.commit();
			if (profile.equals("galego")) {
				setGalicianLocale();
			} else {
				setDefaultLocale();
			}
		}
	}

	class LanguageListAdapter extends ArrayAdapter<String> {

		public LanguageListAdapter(Context context, int resourceId, String[] items) {

			super(context, resourceId, items);
		}

		String[] items;
		ViewHolder holder;

		class ViewHolder {

			ImageView icon;
			TextView title;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.language_list_item, null);

				this.holder = new ViewHolder();
				this.holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				this.holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(this.holder);
			} else {
				// view already defined, retrieve view holder
				this.holder = (ViewHolder) convertView.getTag();
			}

			Drawable tile = ResourceUtils.getDrawable(TradutorGalegoActivity.this,
					LANGUAGES_CODES[position], false);

			this.holder.title.setText(getString(LANGUAGES_RESOURCES_IDS[position]));
			this.holder.icon.setImageDrawable(tile);

			return convertView;
		}
	}

}