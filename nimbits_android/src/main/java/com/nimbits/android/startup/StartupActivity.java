package com.nimbits.android.startup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.nimbits.android.HomeActivity;
import com.nimbits.android.R;
import com.nimbits.android.settings.SettingsActivity;
import com.nimbits.android.AuthenticationManager;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.android.MainActivity;
import com.nimbits.android.startup.async.StartupTask;

import java.util.List;

public class StartupActivity extends Activity {


    private Activity activity;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        ImageView myImageView = (ImageView) findViewById(R.id.nimbits_transparent_logo);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        myImageView.startAnimation(myFadeInAnimation);
        activity = this;







        StartupTask.getInstance(new StartupTask.StartupListener() {
            @Override
            public void onLoginSuccess(List<User> response) {

                Nimbits.session = (response.get(0));
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onLoginFail() {
                CharSequence text = "There was a problem authenticating to Nimbits with the Google Account on this phone. " +
                        "You may need to setup your account first, or set your Base URL to an active instance. Please visit nimbits.com and login to the public cloud first.";
                int duration = Toast.LENGTH_SHORT;
                AuthenticationManager.resetToken(getApplicationContext(), Nimbits.token);
                Toast toast = Toast.makeText(activity, text, duration);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        }).execute(activity);;



    }

}