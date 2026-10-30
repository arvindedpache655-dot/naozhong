package com.example.typereminder;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LockScreenActivity extends Activity {

    private TextView typewriterView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String message = "";
    private int index = 0;
    private Vibrator vibrator;

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            if (index <= message.length()) {
                String current = message.substring(0, index) + (index < message.length() ? " ▌" : "");
                typewriterView.setText(current);
                if (vibrator != null && index % 2 == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(12);
                    }
                }
                index++;
                handler.postDelayed(this, 90);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        message = getIntent().getStringExtra("REMINDER_TEXT");
        if (message == null || message.isEmpty()) {
            message = "时间到了！请查看您的日程安排。";
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#020617"));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        int pad = dp(28);
        root.setPadding(pad, dp(64), pad, dp(48));

        // Top Clock
        TextView clockView = new TextView(this);
        clockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        clockView.setTextColor(Color.WHITE);
        clockView.setTextSize(56f);
        clockView.setGravity(Gravity.CENTER);
        root.addView(clockView);

        TextView subClock = new TextView(this);
        subClock.setText("🔒 锁屏日程正在输入...");
        subClock.setTextColor(Color.parseColor("#38BDF8"));
        subClock.setTextSize(14f);
        subClock.setGravity(Gravity.CENTER);
        subClock.setPadding(0, dp(8), 0, dp(40));
        root.addView(subClock);

        // Card for Typewriter Text
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(24), dp(28), dp(24), dp(28));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.parseColor("#0F172A"));
        cardBg.setCornerRadius(dp(24));
        cardBg.setStroke(dp(1), Color.parseColor("#38BDF8"));
        card.setBackground(cardBg);

        typewriterView = new TextView(this);
        typewriterView.setTextColor(Color.WHITE);
        typewriterView.setTextSize(24f);
        typewriterView.setMinLines(4);
        typewriterView.setLineSpacing(dp(6), 1.1f);
        card.addView(typewriterView);

        root.addView(card);

        // Dismiss Button
        Button btnClose = new Button(this);
        btnClose.setText("我知道了 · 关闭提醒");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setTextSize(16f);
        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setColor(Color.parseColor("#1E293B"));
        closeBg.setCornerRadius(dp(28));
        btnClose.setBackground(closeBg);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        closeParams.setMargins(0, dp(48), 0, 0);
        btnClose.setLayoutParams(closeParams);
        btnClose.setOnClickListener(v -> finish());
        root.addView(btnClose);

        setContentView(root);
        startTypewriter();
    }

    private void startTypewriter() {
        index = 0;
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, 300);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(characterAdder);
        super.onDestroy();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
