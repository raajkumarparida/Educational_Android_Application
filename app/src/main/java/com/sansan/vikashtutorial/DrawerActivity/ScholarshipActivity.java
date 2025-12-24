package com.sansan.vikashtutorial.DrawerActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sansan.vikashtutorial.R;

public class ScholarshipActivity extends AppCompatActivity {

    private ImageView blinkImageView;
    private TextView marqueeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupBlinkAnimation();

    }

    private void initializeViews() {
        marqueeTextView = findViewById(R.id.textView);
        blinkImageView = findViewById(R.id.imageView);

        marqueeTextView.setSelected(true);
    }

    private void setupBlinkAnimation() {
        AlphaAnimation blinkAnimation = new AlphaAnimation(1.0f, 0.0f);
        blinkAnimation.setDuration(500);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        blinkImageView.startAnimation(blinkAnimation);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
