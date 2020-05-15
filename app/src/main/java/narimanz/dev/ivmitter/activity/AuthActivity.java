package narimanz.dev.ivmitter.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import narimanz.dev.ivmitter.R;

public class AuthActivity extends AppCompatActivity {
    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        twitterLoginButton = findViewById(R.id.login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // навигация на экран пользователя
                Long userId = result.data.getUserId();
                navigateToUserInfo(userId);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private void navigateToUserInfo(Long userId) {
        Intent intent = new Intent(AuthActivity.this, UserActivityInfo.class);
        intent.putExtra(UserActivityInfo.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
