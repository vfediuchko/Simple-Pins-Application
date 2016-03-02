package vfediuchko.pins.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.List;

import vfediuchko.pins.test.PreferenceStorage;
import vfediuchko.pins.test.R;


public class RegisterActivity extends AppCompatActivity {

    public static final List<String> PERMISSIONS = Arrays.asList(
            "public_profile",
            "email");

    private CallbackManager callbackManager;
    private View signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (!TextUtils.isEmpty(PreferenceStorage.getActiveUserId())) {
            goToMainActivity();
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        signUpButton = findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                authorizeFacebook();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void authorizeFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, PERMISSIONS);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("FFF", "authorizeFacebook onSuccess");
                        PreferenceStorage.saveActiveUserId(loginResult.getAccessToken().getUserId());
                        goToMainActivity();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FFF", "authorizeFacebook onCancel");
                        hideProgress();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("FFF", "authorizeFacebook onError " + error.getMessage());
                        hideProgress();
                    }
                });
    }

    private void hideProgress() {
        signUpButton.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        signUpButton.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
