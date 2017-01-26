/*
 * Copyright (c) 2017. Behrouz Khezry
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jadi.activity;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import net.jadi.R;
import net.jadi.utility.Utility;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AwesomeSplash {
    private MaterialDialog errorConnectionDialog;

    @Override
    public void initSplash(ConfigSplash configSplash) {

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.md_blue_grey_200); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(2000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP


        //Customize Logo
        configSplash.setLogoSplash(R.mipmap.ic_launcher); //or any other drawable
        configSplash.setAnimLogoSplashDuration(500); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.BounceIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Title
        configSplash.setTitleSplash(getResources().getString(R.string.splash_text));
        configSplash.setTitleTextColor(R.color.md_black_1000);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(250);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
        errorConnectionDialog = new MaterialDialog.Builder(SplashActivity.this)
                .content(R.string.error_connect_label)
                .contentGravity(GravityEnum.END)
                .cancelable(false)
                .autoDismiss(false)
                .typeface("IRANSansMobile.ttf", "IRANSansMobile.ttf")
                .positiveText(R.string.retry_label)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkConnection();
                    }
                }).build();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void animationsFinished() {
        checkConnection();
    }

    private void checkConnection() {
        if (Utility.hasConnection(this)) {
            goMainActivity();
        } else {
            errorConnectionDialog.dismiss();
            errorConnectionDialog.show();
        }
    }
    private void goMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}