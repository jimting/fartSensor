package ntou.jt.fartsensor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends Activity {
    //這個Activity主要是讓使用者登入，登入完就可以進入系統啦~(使用FB登入)

    CallbackManager callbackManager;
    private AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        //初始化FacebookSdk，記得要放第一行，不然setContentView會出錯
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView loginStatus = (TextView)findViewById(R.id.login_status);
        final TextView userName = (TextView)findViewById(R.id.user_name);
        final TextView userID = (TextView)findViewById(R.id.user_id);

        //宣告callback Manager
        callbackManager = CallbackManager.Factory.create();

        //找到button
        Button loginButton = (Button) findViewById(R.id.fb_login);
        final Button startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
        {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            intent.putExtra("Name", userName.getText());
            intent.putExtra("ID", userID.getText());
            startActivity(intent);

        }});

        loginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });

        //幫 LoginManager 增加callback function
        //這邊為了方便 直接寫成inner class
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {
                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();

                Log.d("FB", "access token got.");

                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                System.out.println("FBData:"+object.toString());

                                //設定loginStatus
                                loginStatus.setText(object.optString("name")+" 登入成功！");

                                //設定Name和ID的暫存
                                userID.setText(object.optString("id"));
                                userName.setText(object.optString("name"));

                                //將開始旅行的按鈕顯示
                                startButton.setVisibility(View.VISIBLE);

                                //讀出姓名 ID FB個人頁面連結
                                Log.d("FB", "complete");
                                Log.d("FB", object.optString("name"));
                                Log.d("FB", object.optString("link"));
                                Log.d("FB", object.optString("id"));

                                ImageView mImgPhoto = (ImageView) findViewById(R.id.mImgPhoto);
                                // 取得用戶大頭照
                                Profile profile = Profile.getCurrentProfile();
                                // 設定大頭照大小
                                Uri userPhoto = profile.getProfilePictureUri(500, 500);
                                Glide.with(LoginActivity.this)
                                        .load(userPhoto.toString())
                                        .into(mImgPhoto);
                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.d("FB", "CANCEL");
            }

            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FB", exception.toString());
            }
        });


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}