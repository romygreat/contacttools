package com.longcheercontact.com.contact;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

public class TestService extends IntentService {

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public TestService(String name) {
        super(name);
    }
}
