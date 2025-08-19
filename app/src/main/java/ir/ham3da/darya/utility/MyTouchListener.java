package ir.ham3da.darya.utility;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class MyTouchListener implements View.OnTouchListener {
    private GestureDetector mGestureDetector; // نوع متغیر تغییر کرد
    private Context mContext;

    public MyTouchListener(Context context) {
        mContext = context;
        // ساخت 객체 تغییر کرد
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                // Log.d("DEBUG", "onDown");
                return true; // برای دریافت رویدادهای بعدی باید true برگردانید
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // مدیریت تک ضربه تایید شده
                // Log.d("DEBUG", "onSingleTapConfirmed");
                // اگر رویداد را مدیریت کردید true برگردانید، در غیر این صورت false
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // مدیریت لمس طولانی
                // Log.d("DEBUG", "onLongPress");
            }

            // ... سایر callback های GestureDetector مانند onFling, onDoubleTap و ...
        });

        // (اختیاری) اگر می خواهید رویداد دابل کلیک را هم مدیریت کنید:
        // mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
        //     @Override
        //     public boolean onSingleTapConfirmed(MotionEvent e) {
        //         // اگر می خواهید این از onSingleTapUp در SimpleOnGestureListener جدا باشد
        //         return false;
        //     }
        //
        //     @Override
        //     public boolean onDoubleTap(MotionEvent e) {
        //         // Log.d("DEBUG", "onDoubleTap");
        //         return true;
        //     }
        //
        //     @Override
        //     public boolean onDoubleTapEvent(MotionEvent e) {
        //         return false;
        //     }
        // });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // پاس دادن رویداد لمسی به GestureDetector
        // مقدار بازگشتی نشان می دهد که آیا رویداد مصرف شده است یا خیر
        return mGestureDetector.onTouchEvent(event);
    }
}