package id.pptik.org.generalledger.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import id.pptik.org.generalledger.model.User;

/**
 * Created by Hafid on 9/27/2017.
 */

public class SessionManager {

    public static String TAG = "[PreferenceManager]";

    public static final String KEY_PREFERENCES_NAME = "GeneralLedger";
    public static final String KEY_IS_LOGIN = "IsLogin";
    public static final String KEY_USER_INFOS_JSON = "UserProfile";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Activity activity;

    public SessionManager(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
        pref = context.getSharedPreferences(KEY_PREFERENCES_NAME, context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user){
        Gson gson = new Gson();
        String jsonInString = gson.toJson(user);
        editor.putString(KEY_USER_INFOS_JSON, jsonInString);
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.commit();
    }

    public User getUserDetail(){
        Gson gson = new Gson();
        String jsonInString = pref.getString(KEY_USER_INFOS_JSON, "");
        User user = gson.fromJson(jsonInString, User.class);
        return user;
    }

    public boolean isLogin(){
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public void logout(){
        editor.putBoolean(KEY_IS_LOGIN, false);

        editor.commit();
        activity.finish();
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public void setPref(SharedPreferences pref) {
        this.pref = pref;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
