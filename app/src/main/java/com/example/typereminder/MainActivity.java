package com.example.typereminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {

    public static final String CHANNEL_ID = "lockscreen_typewriter_channel";
    private EditText inputMessage;
    private TimePicker timePicker;
    private TextView statusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#0F172A"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(24);
        root.setPadding(pad, dp(40), pad, pad);

        TextView title = new TextView(this);
        title.setText("锁屏打字提醒");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26f);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("到点自动唤醒锁屏 · 逐字敲出您的日程");
        subtitle.setTextColor(Color.parseColor("#94A3B8"));
        subtitle.setTextSize(14f);
        subtitle.setPadding(0, dp(4), 0, dp(20));
        root.addView(subtitle);

        // Input Box
        inputMessage = new EditText(this);
        inputMessage.setHint("输入要在锁屏上逐字敲出的提醒内容...");
        inputMessage.setHintTextColor(Color.parseColor("#64748B"));
        inputMessage.setTextColor(Color.WHITE);
        inputMessage.setTextSize(16f);
        inputMessage.setMinLines(3);
        inputMessage.setGravity(Gravity.TOP | Gravity.START);
        inputMessage.setPadding(dp(16), dp(16), dp(16), dp(16));
        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(Color.parseColor("#1E293B"));
        inputBg.setCornerRadius(dp(16));
        inputMessage.setBackground(inputBg);

        SharedPreferences prefs = getSharedPreferences("reminder_prefs", MODE_PRIVATE);
        inputMessage.setText(prefs.getString("last_msg", "下午 3:00 参加产品周会，记得带充电器！"));
        root.addView(inputMessage);

        // TimePicker
        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        root.addView(timePicker);

        // Schedule Button
        Button btnSchedule = new Button(this);
        btnSchedule.setText("开启定时锁屏提醒");
        btnSchedule.setTextColor(Color.parseColor("#0F172A"));
        btnSchedule.setTextSize(16f);
        GradientDrawable primaryBg = new GradientDrawable();
        primaryBg.setColor(Color.parseColor("#38BDF8"));
        primaryBg.setCornerRadius(dp(14));
        btnSchedule.setBackground(primaryBg);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        btnParams.setMargins(0, dp(12), 0, dp(12));
        btnSchedule.setLayoutParams(btnParams);
        btnSchedule.setOnClickListener(v -> scheduleAlarm(false));
        root.addView(btnSchedule);

        // 10s Test Button
        Button btnTest10s = new Button(this);
        btnTest10s.setText("10 秒后测试（点击后请立刻按电源键锁屏）");
        btnTest10s.setTextColor(Color.WHITE);
        btnTest10s.setTextSize(14f);
        GradientDrawable secondaryBg = new GradientDrawable();
        secondaryBg.setColor(Color.parseColor("#334155"));
        secondaryBg.setCornerRadius(dp(14));
        btnTest10s.setBackground(secondaryBg);
        LinearLayout.LayoutParams testParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
        testParams.setMargins(0, 0, 0, dp(12));
        btnTest10s.setLayoutParams(testParams);
        btnTest10s.setOnClickListener(v -> scheduleAlarm(true));
        root.addView(btnTest10s);

        // Preview Immediately Button
        Button btnPreview = new Button(this);
        btnPreview.setText("立即预览锁屏打字效果");
        btnPreview.setTextColor(Color.parseColor("#38BDF8"));
        btnPreview.setBackgroundColor(Color.TRANSPARENT);
        btnPreview.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (msg.isEmpty()) msg = "时间到了！请查看您的日程提醒。";
            Intent i = new Intent(MainActivity.this, LockScreenActivity.class);
            i.putExtra("REMINDER_TEXT", msg);
            startActivity(i);
        });
        root.addView(btnPreview);

        statusLabel = new TextView(this);
        statusLabel.setTextColor(Color.parseColor("#34D399"));
        statusLabel.setTextSize(13f);
        statusLabel.setGravity(Gravity.CENTER);
        statusLabel.setPadding(0, dp(12), 0, 0);
        root.addView(statusLabel);

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private void scheduleAlarm(boolean isTenSecondTest) {
        String msg = inputMessage.getText().toString().trim();
        if (msg.isEmpty()) {
            msg = "时间到了！请查看您的日程提醒。";
        }
        getSharedPreferences("reminder_prefs", MODE_PRIVATE)
                .edit().putString("last_msg", msg).apply();

        Calendar cal = Calendar.getInstance();
        String timeDesc;
        if (isTenSecondTest) {
            cal.add(Calendar.SECOND, 10);
            timeDesc = "10 秒后";
        } else {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            timeDesc = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("REMINDER_TEXT", msg);
        PendingIntent pi = PendingIntent.getBroadcast(
                this, 1001, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                statusLabel.setText("已设定：" + timeDesc + " 锁屏打字提醒");
                Toast.makeText(this, "提醒已设定在 " + timeDesc, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                statusLabel.setText("已设定：" + timeDesc + " 锁屏提醒");
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "锁屏打字日程提醒",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("用于在锁屏状态下自动亮屏并弹出打字提醒界面");
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
