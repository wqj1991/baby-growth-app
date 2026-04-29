package com.example.baobao;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaceholderFeatureActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder_feature);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);

        TextView titleView = findViewById(R.id.placeholder_title);
        TextView descriptionView = findViewById(R.id.placeholder_description);

        titleView.setText(title != null ? title : getString(R.string.app_name));
        descriptionView.setText(description != null ? description : "该功能正在开发中，敬请期待。");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_done).setOnClickListener(v -> finish());
    }
}

