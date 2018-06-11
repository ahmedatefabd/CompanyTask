package com.task.ateftask.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.task.ateftask.R;
import com.task.ateftask.model.FacebookUserData;
import com.task.ateftask.util.Connection;
import com.task.ateftask.util.Constant;
import com.task.ateftask.util.JsonParser;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.facebook_login_btn)
    Button facebookLoginBtn;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
        initFacebookLogin();
    }

    private void init() {
        facebookLoginBtn.setOnClickListener(this);
    }

    private void initFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                userGranted(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Facebook Login Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userGranted(AccessToken token) {
        Bundle graphRequestInfo = new Bundle();
        graphRequestInfo.putString(Constant.Graph.FIELDS, Constant.Graph.FIELDS_ATTRIBUTES);
        GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject userData = response.getJSONObject();
                FacebookUserData data = JsonParser.getUserFromJson(userData);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(Constant.Extras.USER, data);
                startActivity(intent);
            }
        });
        graphRequest.setParameters(graphRequestInfo);
        graphRequest.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_login_btn:
                login();
                break;
        }
    }

    private void login() {
        if (Connection.isNetworkAvailable(this))
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(Constant.FacebookPermissions.publicProfilePermission, Constant.FacebookPermissions.emailPermission));
        else {
            Toast.makeText(this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }
    }
}
