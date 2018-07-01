package journal.kwame.com.journal.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;

import journal.kwame.com.journal.Activity.LoginActivity;

/**
 * Created by user on 6/28/2018.
 */

public class UserPreference {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public UserPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constants.PREF_NAME, 0);
        editor = preferences.edit();
    }

    public boolean isUserLoggedIn() {
        return preferences.getBoolean(Constants.IS_LOGGED_IN, false);
    }

    public void saveUserDetails(FirebaseUser user) {
        editor.putBoolean(Constants.IS_LOGGED_IN, true);
        editor.putString(Constants.TOKEN, user.getUid());
        editor.putString(Constants.EMAIL, user.getEmail());
        editor.putString(Constants.IMAGE, String.valueOf(user.getPhotoUrl()));
        editor.putString(Constants.NAME, user.getDisplayName());

        editor.commit();
    }

    public void redirectUserToLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String getUserToken() {
        return preferences.getString(Constants.TOKEN, null);
    }

    public void clearUserDetails() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
