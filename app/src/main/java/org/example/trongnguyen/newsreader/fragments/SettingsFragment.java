package org.example.trongnguyen.newsreader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.example.trongnguyen.newsreader.R;

import java.util.Arrays;
import java.util.Set;

import static android.support.constraint.Constraints.TAG;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public static class NewsPreferenceFragment extends PreferenceFragment {
        MultiSelectListPreference source;
        String themeChooserText;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            themeChooserText = prefs.getString("theme", "1");

            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_main);

            source = (MultiSelectListPreference) findPreference("multiple_choice_prefs");
            source.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Will only process the changes if there is more than 1 item checked. Otherwise refuse change.
                    if (newValue.toString().equals("[]")) {
                        Toast.makeText(getActivity(), "Unable to save preferences. Please pick at least one source!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }
            });

        }
    }

}
