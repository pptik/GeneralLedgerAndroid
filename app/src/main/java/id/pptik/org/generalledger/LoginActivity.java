package id.pptik.org.generalledger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.User;
import id.pptik.org.generalledger.tools.GerenalLedgerRestAPI;
import id.pptik.org.generalledger.tools.SessionManager;

public class LoginActivity extends AppCompatActivity {
    public static String TAG = "[LoginActivity]";

    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btnSignIn)
    Button btnSignin;
    @BindView(R.id.btnSignUP)
    Button btnSignup;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    SessionManager sessionManager;
    private boolean checkPermission = false;

    private static final int MY_PERMISSIONS_REQUEST = 999;
    String[] listPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this, getApplicationContext());

        if (sessionManager.isLogin()){
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcces();
                //Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                //startActivity(intent);
                //finish();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        setPermission();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST:
                checkPermission = true;
        }
    }

    private void loginProcces(){
        String phonenum = phoneNumber.getText().toString();
        String password_usr = password.getText().toString();
        RequestParams params = new RequestParams();
        params.put("noHp",phonenum);
        params.put("password",password_usr);
        GerenalLedgerRestAPI.post(Constants.REST_LOGIN_USER,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Gson g = new Gson();
                        JSONObject userJSON = response.getJSONObject("profile");
                        User user_apps = g.fromJson(userJSON.toString(), User.class);
                        sessionManager.createLoginSession(user_apps);
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response.getString("message");
                        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void setPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, listPermission, MY_PERMISSIONS_REQUEST);
        }
    }
}
