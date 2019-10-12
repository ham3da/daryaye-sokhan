package ir.ham3da.darya;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class Bungee {

    public static void slideLeft(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public static void slideRight(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void slideDown(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit);
    }

    public static void slideUp(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit);
    }

    public static void zoom(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public static void fade(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.fade_enter, R.anim.fade_exit);
    }

    public static void windmill(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.windmill_enter, R.anim.windmill_exit);
    }

    public static void spin(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.spin_enter, R.anim.spin_exit);
    }

    public static void diagonal(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.diagonal_right_enter, R.anim.diagonal_right_exit);
    }

    public static void split(Context context){
        ((AppCompatActivity) context).overridePendingTransition(R.anim.split_enter, R.anim.split_exit);
    }

    public static void shrink(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.shrink_enter, R.anim.shrink_exit);
    }

    public static void card(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.card_enter, R.anim.card_exit);
    }

    public static void inAndOut(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.in_out_enter, R.anim.in_out_exit);
    }

    public static void swipeLeft(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.swipe_left_enter, R.anim.swipe_left_exit);
    }

    public static void swipeRight(Context context) {
        ((AppCompatActivity) context).overridePendingTransition(R.anim.swipe_right_enter, R.anim.swipe_right_exit);
    }
}
