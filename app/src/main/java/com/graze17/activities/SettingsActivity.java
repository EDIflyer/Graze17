package com.graze17.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.graze17.DashboardListActivity;
import com.graze17.EntryManager;
import com.graze17.IEntryModelUpdateListener;
import com.graze17.R;
import com.graze17.jobs.ModelUpdateResult;
import com.graze17.preference.ListPreference;
import com.graze17.util.SDKVersionUtil;

public class SettingsActivity extends PreferenceActivity implements IEntryModelUpdateListener
{

  private Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    final EntryManager em = EntryManager.getInstance(this);

    PreferenceManager.setDefaultValues(this, EntryManager.PREFERENCES_NAME, MODE_PRIVATE, R.xml.settings, false);
    getPreferenceManager().setSharedPreferencesName(EntryManager.PREFERENCES_NAME);
    addPreferencesFromResource(R.xml.settings);

    applyEdgeToEdge(getListView());

    getPreferenceScreen().setOnPreferenceChangeListener(em);

    if (SDKVersionUtil.getVersion() < 8)
      disableSetting(em, EntryManager.SETTINGS_PLUGINS, "Froyo+");

    if (SDKVersionUtil.getVersion() < 11)
    {
      disableSetting(em, EntryManager.SETTINGS_HW_ACCEL_LISTS_ENABLED, "HC+ only");
      disableSetting(em, EntryManager.SETTINGS_HW_ACCEL_ADV_ENABLED, "HC+ only");
    }

    if (em.shouldHWZoomControlsBeDisabled())
    {
      Preference pref = getPreferenceScreen().findPreference(EntryManager.SETTINGS_HOVERING_ZOOM_CONTROLS_ENABLED);
      if (pref != null)
      {
        pref.setEnabled(false);
        if (pref.getSummary() != null)
          pref.setSummary("Disabled until HTC fixes a bug that hurts this function. Sorry.");
      }
    }

    if (em.shouldSyncInProgressNotificationBeDisabled())
    {
      Preference pref = getPreferenceScreen().findPreference(EntryManager.SETTINGS_SYNC_IN_PROGRESS_NOTIFICATION);
      if (pref != null)
      {
        pref.setEnabled(false);
        if (pref.getSummary() != null)
          pref.setSummary("Disabled until HTC/Dell fixes a bug that hurts this function. Sorry.");
      }
    }

    if (em.shouldActionBarLocationOnlyAllowGone())
    {
      ListPreference pref = (ListPreference) getPreferenceScreen().findPreference(EntryManager.SETTINGS_UI_ACTION_BAR_LOCATION);
      if (pref != null)
      {
        pref.setEnabled(false);
        CharSequence[] seq = pref.getEntries();
        CharSequence[] newSeq = new CharSequence[] { seq[2] };
        pref.setEntries(newSeq);

        getPreferenceScreen().removePreference(pref);
      }
    }

    // Add click listeners for About section preferences
    Preference aboutLicensePref = findPreference("about_license_preference");
    if (aboutLicensePref != null) {
      aboutLicensePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          showLicenseDialog();
          return true;
        }
      });
    }
    
    Preference aboutGithubPref = findPreference("about_github_preference");
    if (aboutGithubPref != null) {
      aboutGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          openGithubRepository();
          return true;
        }
      });
    }

    Preference aboutVersionPref = findPreference("about_version_preference");
    if (aboutVersionPref != null) {
      aboutVersionPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          showVersionInfoDialog();
          return true;
        }
      });
    }

  }

  @Override
  public void onContentChanged() {
    super.onContentChanged();
    applyEdgeToEdge(getListView());
  }

  private void applyEdgeToEdge(View view) {
    if (view == null || SDKVersionUtil.getVersion() < 21) {
      return;
    }

    if (view instanceof android.widget.ListView) {
      android.widget.ListView lv = (android.widget.ListView) view;
      lv.setClipToPadding(false);
      
      ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
        return insets; // Pass along
      });
      ViewCompat.requestApplyInsets(view);
    }
  }

  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    boolean result = super.onPreferenceTreeClick(preferenceScreen, preference);
    if (preference instanceof PreferenceScreen) {
      PreferenceScreen screen = (PreferenceScreen) preference;
      
      Runnable applyFix = () -> {
        Dialog dialog = screen.getDialog();
        if (dialog != null && dialog.getWindow() != null) {
          // Long nested screens (e.g. User Interface) render with a wrap-content
          // window that can be taller than the display and get clipped above the top.
          dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
          View decorView = dialog.getWindow().getDecorView();
          View lv = findListView(decorView);
          if (lv != null) {
            applyEdgeToEdge(lv);
          }
        }
      };

      handler.post(applyFix);
      handler.postDelayed(applyFix, 10);
    }
    return result;
  }

  private View findListView(View v) {
    if (v instanceof android.widget.ListView) {
      return v;
    }
    if (v instanceof android.view.ViewGroup) {
      android.view.ViewGroup vg = (android.view.ViewGroup) v;
      for (int i = 0; i < vg.getChildCount(); i++) {
        View child = findListView(vg.getChildAt(i));
        if (child != null) return child;
      }
    }
    return null;
  }

  private void disableSetting(EntryManager em, String keyOfPref)
  {
    disableSetting(em, keyOfPref, "PRO");
  }

  private void disableSetting(EntryManager em, String keyOfPref, String reason)
  {
    Preference pref = getPreferenceScreen().findPreference(keyOfPref);
    if (pref != null)
    {
      pref.setEnabled(false);
      if (pref.getTitle() != null)
        pref.setTitle(pref.getTitle() + " (" + reason + ")");
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    EntryManager em = EntryManager.getInstance(this);
    manageModelRelatedSettingsState(em.isModelCurrentlyUpdated());
    em.addListener(this);
  }

  @Override
  protected void onPause()
  {
    EntryManager.getInstance(this).removeListener(this);
    super.onPause();
  }

  public void modelUpdateFinished(ModelUpdateResult result)
  {
    manageModelRelatedSettingsState(false);
  }

  public void modelUpdateStarted(boolean fastSyncOnly)
  {
    manageModelRelatedSettingsState(true);
  }

  public void modelUpdated()
  {
  }

  private void manageModelRelatedSettingsState(final boolean newState)
  {
    handler.post(new Runnable()
    {
      public void run()
      {
        Preference storageProviderPref = getPreferenceManager().findPreference(EntryManager.SETTINGS_STORAGE_PROVIDER_KEY);
        storageProviderPref.setEnabled(!newState);
      }
    });
  }

  public void statusUpdated()
  {
  }

  public void modelUpdated(String atomId)
  {

  }

  private void showLicenseDialog()
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.license)
           .setMessage(R.string.license_text)
           .setIcon(android.R.drawable.ic_dialog_info)
           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
             }
           });
    builder.create().show();
  }

  private void openGithubRepository()
  {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EDIflyer/Graze17"));
    startActivity(browserIntent);
  }

  private void showVersionInfoDialog()
  {
    String message = getString(R.string.about_version_name_label, com.graze17.BuildConfig.VERSION_NAME)
            + "\n" + getString(R.string.about_version_code_label, com.graze17.BuildConfig.VERSION_CODE)
            + "\n" + getString(R.string.about_build_date_label, com.graze17.BuildConfig.BUILD_DATE)
            + "\n" + getString(R.string.about_commit_hash_label, com.graze17.BuildConfig.GIT_COMMIT_HASH);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.about_version_title)
           .setMessage(message)
           .setIcon(R.drawable.grazerss_logo_32x32)
           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
             }
           });
    builder.create().show();
  }
}
