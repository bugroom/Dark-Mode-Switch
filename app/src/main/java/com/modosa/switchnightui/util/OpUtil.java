package com.modosa.switchnightui.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;

import com.modosa.switchnightui.R;

/**
 * @author dadaewq
 */
public class OpUtil {
    public static final String SP_KEY_ENABLE_BUG_REPORT = "enableBugReport";
    public final static String CONFIRM_PROMPT = "ConfirmPrompt01";

    public static void showAlertDialog(Context context, AlertDialog alertDialog) {
        Window window = alertDialog.getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.Background, null)));
            } else {
                window.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.Background)));
            }
            window.setBackgroundDrawableResource(R.drawable.alertdialog_background);
        }

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.rBackground, null));
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.rBackground, null));
            } else {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.rBackground));
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.rBackground));
            }
        }
    }

    static void copyCmd(Context context, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clipData);
    }

    public static void startMyClass(Context context, Class myClass) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setClass(context, myClass);
            context.startActivity(intent);
        } catch (Exception e) {
            showToast1(context, "" + e);
        }
    }

    public static AlertDialog createDialogConfirmPrompt(Context context) {
        SpUtil spUtil = new SpUtil(context);
        View checkBoxView = View.inflate(context, R.layout.confirm_checkbox, null);

        CheckBox checkboxBugreport = checkBoxView.findViewById(R.id.confirm_checkboxBugreport);
        CheckBox checkBox1 = checkBoxView.findViewById(R.id.confirm_checkbox1);
        CheckBox checkBox2 = checkBoxView.findViewById(R.id.confirm_checkbox2);
        CheckBox checkBox3 = checkBoxView.findViewById(R.id.confirm_checkbox3);
        CheckBox checkBox4 = checkBoxView.findViewById(R.id.confirm_checkbox4);
        CheckBox checkBoxLast = checkBoxView.findViewById(R.id.confirm_checkbox_last);
        checkboxBugreport.setText(R.string.checkbox_enable_bugReport);
        checkBox1.setText(R.string.checkbox1_instructions_before_use);
        checkBox2.setText(R.string.checkbox2_instructions_before_use);
        checkBox3.setText(R.string.checkbox3_instructions_before_use);
        checkBox4.setText(R.string.checkbox4_instructions_before_use);
        checkBoxLast.setText(R.string.checkbox_last_instructions_before_use);

        checkboxBugreport.setChecked(spUtil.getBoolean(SP_KEY_ENABLE_BUG_REPORT, true));
        checkBox2.setEnabled(false);
        checkBox3.setEnabled(false);
        checkBox4.setEnabled(false);
        checkBoxLast.setEnabled(false);

        checkboxBugreport.setOnCheckedChangeListener((buttonView, isChecked) -> spUtil.putBoolean(SP_KEY_ENABLE_BUG_REPORT, checkboxBugreport.isChecked()));

        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(isChecked);
        });

        checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkBox3.setChecked(false);
            checkBox3.setEnabled(isChecked);
        });

        checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkBox4.setChecked(false);
            checkBox4.setEnabled(isChecked);
        });

        checkBox4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkBoxLast.setChecked(false);
            checkBoxLast.setEnabled(isChecked);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.title_instructions_before_use)
                .setView(checkBoxView)
                .setPositiveButton(android.R.string.cancel, null)
                .setNeutralButton(android.R.string.ok, (dialog, which) -> {
                    boolean hasBothConfirm = false;
                    if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked() && checkBoxLast.isChecked()) {
                        hasBothConfirm = true;
                    }
                    spUtil.putBoolean(CONFIRM_PROMPT, hasBothConfirm);
                });

        return builder.create();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void finishAndRemoveTask(Activity activity) {
        boolean removeTask = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && new SpUtil(activity).getFalseBoolean("excludeFromRecents")) {
            removeTask = true;
        }
        if (removeTask) {
            activity.finishAndRemoveTask();
        } else {
            activity.finish();
        }
    }


    public static void showDialogConfirmPrompt(Context context, AlertDialog alertDialog) {

        OpUtil.showAlertDialog(context, alertDialog);

        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        button.setEnabled(false);

        CountDownTimer timer = new CountDownTimer(30000, 1000) {
            final String oK = context.getString(android.R.string.ok);

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                button.setText(oK + "(" + millisUntilFinished / 1000 + "s" + ")");
            }

            @Override
            public void onFinish() {
                button.setText(oK);
                button.setEnabled(true);
            }
        };
        //调用 CountDownTimer 对象的 start() 方法开始倒计时，也不涉及到线程处理
        timer.start();
    }

    public static void launchCustomTabsUrl(Context context, String url) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();

            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            showToast1(context, "" + e);
        }
    }

    public static void openUrl(Context context, String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim())));
        } catch (Exception e) {
            showToast1(context, "" + e);
        }
    }

    public static void showToast0(Context context, final String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast0(Context context, final int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast1(Context context, final String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showToast1(Context context, final int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
    }
}
