package alih.androidtictactoe;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by alih on 3/5/15.
 */
public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);
    }
}
