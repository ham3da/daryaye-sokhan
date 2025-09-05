package ir.ham3da.darya;

import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public class Bungee {

    private static void applyTransition(Context context, int enterAnim, int exitAnim) {
        AppCompatActivity activity = (AppCompatActivity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
            activity.overrideActivityTransition(
                    AppCompatActivity.OVERRIDE_TRANSITION_OPEN,
                    enterAnim,
                    exitAnim
            );
        } else {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public static void slideLeft(Context context) {
        applyTransition(context, R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public static void slideRight(Context context) {
        applyTransition(context, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void slideDown(Context context) {
        applyTransition(context, R.anim.slide_down_enter, R.anim.slide_down_exit);
    }

    public static void slideUp(Context context) {
        applyTransition(context, R.anim.slide_up_enter, R.anim.slide_up_exit);
    }

    public static void zoom(Context context) {
        applyTransition(context, R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public static void fade(Context context) {
        applyTransition(context, R.anim.fade_enter, R.anim.fade_exit);
    }

    public static void windmill(Context context) {
        applyTransition(context, R.anim.windmill_enter, R.anim.windmill_exit);
    }

    public static void spin(Context context) {
        applyTransition(context, R.anim.spin_enter, R.anim.spin_exit);
    }

    public static void diagonal(Context context) {
        applyTransition(context, R.anim.diagonal_right_enter, R.anim.diagonal_right_exit);
    }

    public static void split(Context context) {
        applyTransition(context, R.anim.split_enter, R.anim.split_exit);
    }

    public static void shrink(Context context) {
        applyTransition(context, R.anim.shrink_enter, R.anim.shrink_exit);
    }

    public static void card(Context context) {
        applyTransition(context, R.anim.card_enter, R.anim.card_exit);
    }

    public static void inAndOut(Context context) {
        applyTransition(context, R.anim.in_out_enter, R.anim.in_out_exit);
    }

    public static void swipeLeft(Context context) {
        applyTransition(context, R.anim.swipe_left_enter, R.anim.swipe_left_exit);
    }

    public static void swipeRight(Context context) {
        applyTransition(context, R.anim.swipe_right_enter, R.anim.swipe_right_exit);
    }
}
