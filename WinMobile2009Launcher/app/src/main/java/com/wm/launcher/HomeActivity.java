package com.wm.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends Activity {

    private static final int WM_BLUE = Color.rgb(0, 120, 215);
    private static final int WM_DARK = Color.rgb(12, 46, 107);
    private static final int WM_SILVER = Color.rgb(238, 241, 244);
    private static final int WM_LIGHT = Color.rgb(214, 218, 224);
    private static final int WM_BLACK = Color.rgb(20, 22, 26);
    private static final int WM_ACCENT = Color.rgb(72, 160, 240);
    private static final int WM_WHITE = Color.WHITE;

    private FrameLayout root;
    private View startView;
    private boolean startOpen = false;

    private final SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ru"));
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", new Locale("ru"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(WM_DARK);
        getWindow().setNavigationBarColor(WM_DARK);
        buildRoot();
        setContentView(root);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private int dpf(float v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void buildRoot() {
        root = new FrameLayout(this);

        LinearLayout todayColumn = new LinearLayout(this);
        todayColumn.setOrientation(LinearLayout.VERTICAL);

        // Header bar
        TextView header = new TextView(this);
        header.setText("Windows Mobile");
        header.setTextColor(Color.WHITE);
        header.setTextSize(18f);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setPadding(dp(14), dp(8), dp(14), dp(8));
        header.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        header.setBackgroundColor(WM_DARK);
        todayColumn.addView(header, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)));

        // Scrollable content
        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(10), dp(10), dp(10), dp(10));

        content.addView(makeClockCard());

        // Notification strip
        TextView notify = new TextView(this);
        notify.setText("Нет новых сообщений\n1 пропущенный вызов");
        notify.setTextColor(WM_BLACK);
        notify.setTextSize(15f);
        notify.setPadding(dp(12), dp(10), dp(12), dp(10));
        notify.setBackgroundColor(WM_LIGHT);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nlp.topMargin = dp(8);
        content.addView(notify, nlp);

        TextView title = new TextView(this);
        title.setText("Программы");
        title.setTextColor(WM_BLACK);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextSize(16f);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = dp(12);
        content.addView(title, tlp);

        scroll.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        todayColumn.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        // Bottom soft-key bar
        todayColumn.addView(makeSoftKeyBar(true), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(44)));

        // Start menu overlay
        startView = buildStartMenu();
        startView.setVisibility(View.GONE);

        root.addView(todayColumn, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(startView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private LinearLayout makeClockCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(new RoundBg(WM_BLUE, WM_ACCENT));
        card.setElevation(dpf(2f));

        TextView time = new TextView(this);
        time.setText(timeFmt.format(new Date()));
        time.setTextColor(Color.WHITE);
        time.setTypeface(Typeface.DEFAULT_BOLD);
        time.setTextSize(44f);
        time.setGravity(Gravity.CENTER);

        TextView date = new TextView(this);
        date.setText(Character.toUpperCase(dateFmt.format(new Date()).charAt(0))
                + dateFmt.format(new Date()).substring(1));
        date.setTextColor(Color.WHITE);
        date.setTextSize(15f);
        date.setGravity(Gravity.CENTER);
        date.setPadding(0, dp(2), 0, 0);

        card.addView(time, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(64)));
        card.addView(date, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private LinearLayout makeSoftKeyBar(boolean isBottom) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(WM_DARK);

        TextView startBtn = keyButton("Start");
        TextView appsBtn = keyButton("Программы");
        TextView spacer = new TextView(this);
        spacer.setBackgroundColor(WM_DARK);

        bar.addView(startBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        bar.addView(spacer, dp(2));
        bar.addView(appsBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        startBtn.setOnClickListener(v -> toggleStart());
        if (isBottom) {
            appsBtn.setOnClickListener(v -> openAppsDialog());
        }
        return bar;
    }

    private TextView keyButton(String label) {
        TextView b = new TextView(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundColor(WM_DARK);
        return b;
    }

    private void openAppsDialog() {
        List<AppInfo> apps = installedApps();
        if (apps.isEmpty()) {
            Toast.makeText(this, "Нет приложений", Toast.LENGTH_LONG).show();
            return;
        }
        String[] names = new String[apps.size()];
        for (int i = 0; i < apps.size(); i++) {
            names[i] = apps.get(i).label;
        }
        new AlertDialog.Builder(this)
                .setTitle("Программы")
                .setItems(names, (d, i) -> launchApp(apps.get(i)))
                .setNegativeButton("Отмена", null)
                .show();
    }

    private static class AppInfo {
        final String label;
        final ComponentName activity;
        AppInfo(String label, ComponentName activity) {
            this.label = label;
            this.activity = activity;
        }
    }

    private List<AppInfo> installedApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        List<AppInfo> out = new ArrayList<>();
        for (ResolveInfo ri : list) {
            ComponentName cn = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
            String label = ri.activityInfo.loadLabel(getPackageManager()).toString();
            out.add(new AppInfo(label, cn));
        }
        Collections.sort(out, Comparator.comparing(a -> a.label.toLowerCase(Locale.getDefault())));
        return out;
    }

    void launchApp(AppInfo info) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(info.activity);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Не удалось запустить", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleStart() {
        startOpen = !startOpen;
        startView.setVisibility(startOpen ? View.VISIBLE : View.GONE);
    }

    private void dismissStart() {
        startOpen = false;
        startView.setVisibility(View.GONE);
    }

    private View buildStartMenu() {
        FrameLayout wrap = new FrameLayout(this);

        View dim = new View(this);
        dim.setBackgroundColor(Color.argb(120, 0, 0, 0));
        dim.setOnClickListener(v -> dismissStart());
        wrap.addView(dim, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(WM_SILVER);

        TextView header = new TextView(this);
        header.setText("Start");
        header.setTextColor(Color.WHITE);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setTextSize(16f);
        header.setPadding(dp(12), dp(6), dp(12), dp(6));
        header.setBackgroundColor(WM_DARK);
        panel.addView(header, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44)));

        ScrollView appScroll = new ScrollView(this);
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setPadding(dp(8), dp(8), dp(8), dp(8));

        List<AppInfo> apps = installedApps();
        if (apps.isEmpty()) {
            grid.addView(makeMsg("Нет приложений"));
        }
        int i = 0;
        while (i < apps.size()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 4; j++) {
                if (i < apps.size()) {
                    row.addView(makeTile(apps.get(i)),
                            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                } else {
                    row.addView(new TextView(this),
                            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                }
                i++;
            }
            grid.addView(row, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        appScroll.addView(grid, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        panel.addView(appScroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        panel.addView(makeSoftKeyBar(false), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(44)));

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 9 / 10,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.START;
        wrap.addView(panel, lp);
        return wrap;
    }

    private TextView makeMsg(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(14f);
        t.setTextColor(WM_BLACK);
        t.setPadding(dp(8), dp(8), dp(8), dp(8));
        return t;
    }

    private View makeTile(AppInfo info) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setPadding(dp(8), dp(8), dp(8), dp(8));
        tile.setBackground(new RoundBg(WM_WHITE, WM_LIGHT));
        tile.setElevation(dpf(1f));

        TextView icon = new TextView(this);
        icon.setText("\u25AA");
        icon.setTextColor(WM_BLUE);
        icon.setTextSize(28f);
        icon.setGravity(Gravity.CENTER);

        TextView label = new TextView(this);
        label.setText(info.label);
        label.setTextColor(WM_BLACK);
        label.setTextSize(11f);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);

        tile.addView(icon, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44)));
        tile.addView(label, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tile.setOnClickListener(v -> launchApp(info));
        return tile;
    }

    private class RoundBg extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF boundsF = new RectF();
        private final int fill;
        private final int border;
        RoundBg(int fill, int border) {
            this.fill = fill;
            this.border = border;
        }
        @Override
        public void draw(Canvas canvas) {
            boundsF.set(getBounds());
            paint.setColor(fill);
            canvas.drawRoundRect(boundsF, dpf(6f), dpf(6f), paint);
            paint.setColor(border);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dpf(1f));
            canvas.drawRoundRect(boundsF, dpf(6f), dpf(6f), paint);
            paint.setStyle(Paint.Style.FILL);
        }
        @Override
        public void setAlpha(int alpha) {}
        @Override
        public int getOpacity() { return PixelFormat.OPAQUE; }
        @Override
        public void setColorFilter(android.graphics.ColorFilter cf) {}
    }

    @Override
    public void onBackPressed() {
        if (startOpen) dismissStart();
        else super.onBackPressed();
    }
}