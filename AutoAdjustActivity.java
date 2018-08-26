package io.wallmag.fontviews.AutoAdjustActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Ajeet Kumar Meena on 2/23/17.
 */

public abstract class AutoAdjustActivity extends AppCompatActivity implements KeyboardHeightObserver {

    private KeyboardHeightProvider keyboardHeightProvider;
    private int contentViewInitialHeight;
    private boolean isKeyboardOpen;

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        Log.i("onKeyboardHeightChanged", "onKeyboardHeightChanged in pixels: " + height + " " + or);
        View contentView = findViewById(android.R.id.content);
        contentView.getLayoutParams().height = contentViewInitialHeight - height;
        contentView.requestLayout();
        isKeyboardOpen = height != 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        findViewById(android.R.id.content).post(new Runnable() {
            public void run() {
                contentViewInitialHeight = findViewById(android.R.id.content).getMeasuredHeight();
                keyboardHeightProvider.start();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        if (isKeyboardOpen) {
            View contentView = findViewById(android.R.id.content);
            contentView.getLayoutParams().height = contentViewInitialHeight;
            contentView.requestLayout();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }
}
