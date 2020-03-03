package com.example.vkphoto.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.vkphoto.R;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!VK.isLoggedIn())
            VK.login(this, Arrays.asList(VKScope.FRIENDS, VKScope.PHOTOS));
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        VKAuthCallback callback = new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLoginFailed(int i) {
                Toast.makeText(LoginActivity.this, R.string.login_failure, Toast.LENGTH_SHORT).show();
            }
        };

        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
