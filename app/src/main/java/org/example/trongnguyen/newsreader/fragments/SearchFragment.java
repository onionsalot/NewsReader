package org.example.trongnguyen.newsreader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.example.trongnguyen.newsreader.R;

import java.util.ArrayList;
import java.util.Set;

import static android.support.constraint.Constraints.TAG;

public class SearchFragment extends Fragment {
    View rootView;
    private SharedPreferences sp;
    ListView listView;
    EditText editText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        editText = (EditText) rootView.findViewById(R.id.search_user_input);
        listView = (ListView) rootView.findViewById(R.id.source_list_view);
        Button button = (Button) rootView.findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItems(v);
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set "sources" to the items checked in "multiple_choice_prefs"
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> sources = sp.getStringSet("multiple_choice_prefs", null);
        // Create a string of "items" which will be used by the adapter to print out a predetermined list
        String[] items = {
                "Arstechnica",
                "Wired",
                "Reuters",
                "CNBC",
                "Washington Post",
                "WallStreet Journal",
                "Daily Caller",
                "Polygon",
                "Siliconera",
                "Gamespot",
                "Anime News Network",
                "Crunchyroll"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_search_item,R.id.checked_text, items);
        listView.setAdapter(adapter);

        // Listener to check if the checked or unchecked item is the last one.
        // This will be to prevent the user from being able to uncheck all items.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getCheckedItemCount() == 0) {
                    listView.setItemChecked(position,true);
                    Toast.makeText(getContext(),  "Failed! Please have at least one item checked!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Loop to check if "sources" contains any items within the "items" string and check it if it does.
        // This is how I determine which fields to auto check as these are the ones defined by the user preferences.
        // The user is free to uncheck or check more items if they so wish. The preferences will NOT change.
        // This is meant to be it's own entity and should not be mixed with the user preferences.
        for (int i = 0; i < 12; i++ ) {
            if (sources.contains(items[i].replace(" ", "_").toLowerCase())) {listView.setItemChecked(i, true);}
        }
        return rootView;
    }

    public void searchItems(View view){
        // TODO: create a check all button
        Log.d(TAG, "showSelectedItems: " + listView.getCheckedItemCount() );
        StringBuilder stringBuilder = new StringBuilder();
        // StringBuilder will check if user has checked each of the sources, then append the source.
        stringBuilder.append("(");
        for (int i = 0; i < 12; i++ ) {
            if( listView.isItemChecked(i)) {
                stringBuilder.append("site%3A");
                stringBuilder.append(listView.getItemAtPosition(i).toString().replace(" ", "").toLowerCase());
                stringBuilder.append(".com%20OR%20");
            }
        }
        stringBuilder.replace(stringBuilder.length()-5,stringBuilder.length(),"");
        stringBuilder.append(")");

        String userInput = editText.getText().toString().trim();
        if (!(userInput.equals(""))) {
            stringBuilder.append("%20thread.title%3A").append(userInput);
        }

        String searchURL = stringBuilder.toString();
        searchURL = searchURL.replace("wallstreetjournal","wsj");
        Log.d(TAG, "showSelectedItems: String Builder " + searchURL);


        Fragment secondFragment = new MainFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("URL", searchURL);

        secondFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, secondFragment, "searchResults");
        transaction.addToBackStack("searchStack");
        transaction.commit();
    }
}
