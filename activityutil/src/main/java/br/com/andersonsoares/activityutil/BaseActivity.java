package br.com.andersonsoares.activityutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by devmaker on 22/06/17.
 */

public class BaseActivity extends AppCompatActivity {

    public void hideKeybord(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void initStatusBar(View toolbar) {
        // Ensure `setStatusBarImmersiveMode()`
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            // Ensure content view `fitsSystemWindows` is false.
            ViewGroup contentParent = (ViewGroup) findViewById(android.R.id.content);
            View content = contentParent.getChildAt(0);
            // If using `DrawerLayout`, must ensure its subviews `fitsSystemWindows` are all false.
            // Because in some roms, such as MIUI, it will fits system windows for each subview.
            setFitsSystemWindows(content, false, true);

            // Add padding to hold the status bar place.
            clipToStatusBar(toolbar);

            // Add a view to hold the status bar place.
            // Note: if using appbar_scrolling_view_behavior of CoordinatorLayout, however,
            // the holder view could be scrolled to outside as it above the app bar.
            //holdStatusBar(toolbar, R.color.colorPrimary);
        }
    }

    protected void setFitsSystemWindows(View view, boolean fitSystemWindows, boolean applyToChildren) {
        if (view == null) return;
        view.setFitsSystemWindows(fitSystemWindows);
        if (applyToChildren && (view instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
                viewGroup.getChildAt(i).setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    protected void clipToStatusBar(View view) {
        final int statusBarHeight = getStatusBarHeight(this);
        view.getLayoutParams().height += statusBarHeight;
        view.setPadding(0, statusBarHeight, 0, 0);
    }


    protected int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void setStatusBarImmersiveMode(@ColorInt int color) {
        Window win = getWindow();

        // StatusBar
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 21) { // 21, 5.0, LOLLIPOP
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(color);
        }

//        // Setup immersive mode on third-party rom
//        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
//            //FlymeUtils.setStatusBarDarkIcon(win, false);
//            MIUIUtils.setStatusBar(win, MIUIUtils.StatusBarMode.TRANSPARENT);
//        }

    }

    ProgressDialog progressDialog;
    public void showDialog(){
        showDialog("Buscando dados...");
    }

    public void showDialog(String messange){
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(messange);
        progressDialog.show();
    }

    public void dismissDialog(){
        if( progressDialog != null)
            progressDialog.dismiss();
    }


    public void showDialog(String title,String message) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } catch (Exception var4) {
            Log.e("showDialog", var4.toString(), (Throwable)var4);
        }

    }

    public void showDialog(   String title,   String message,   final BaseActivity.OnClickListener callback) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence)title).setMessage((CharSequence)message)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialog, int id) {
                    if(callback != null) {
                        callback.onPositive(dialog);
                    } else {
                        dialog.dismiss();
                    }

                }
            });
            builder.create().show();
        } catch (Exception var5) {
            Log.e("showDialog", var5.toString(), (Throwable)var5);
        }

    }

    public void showDialog(   String title,   String message,   String positive,   String negative,   final BaseActivity.OnClickListener callback) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positive,new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialog, int id) {
                    if(callback != null) {
                        callback.onPositive(dialog);
                    } else {
                        dialog.dismiss();
                    }

                }
            }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialog, int id) {
                    if(callback != null) {
                        callback.onNegative(dialog);
                    } else {
                        dialog.dismiss();
                    }

                }
            });
            builder.create().show();
        } catch (Exception var7) {
            Log.e("showDialog", var7.toString(), (Throwable)var7);
        }

    }

    public interface OnClickListener {
        void onPositive(DialogInterface dialog);
        void onNegative(DialogInterface dialog);
    }
}
