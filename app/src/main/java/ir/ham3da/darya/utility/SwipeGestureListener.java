package ir.ham3da.darya.utility;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SwipeGestureListener implements RecyclerView.OnItemTouchListener {

    public interface OnSwipeListener {
        void onSwipeLeft(int position);
        void onSwipeRight(int position);
    }

    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;
    private OnSwipeListener swipeListener;

    // حساسیت‌ها
    private static final int MIN_SWIPE_DISTANCE = 200; // قبلاً 100 بود
    private static final int MIN_SWIPE_VELOCITY = 300; // اختیاری

    public SwipeGestureListener(Context context, RecyclerView recyclerView, OnSwipeListener listener) {
        this.recyclerView = recyclerView;
        this.swipeListener = listener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();

                // فقط زمانی که حرکت افقی خیلی بیشتر از عمودی باشه و فاصله زیاد باشه
                if (Math.abs(deltaX) > Math.abs(deltaY) * 2 &&
                        Math.abs(deltaX) > MIN_SWIPE_DISTANCE &&
                        Math.abs(velocityX) > MIN_SWIPE_VELOCITY) {

                    View child = recyclerView.findChildViewUnder(e1.getX(), e1.getY());
                    if (child != null) {
                        int position = recyclerView.getChildAdapterPosition(child);
                        if (deltaX > 0) {
                            swipeListener.onSwipeRight(position);
                        } else {
                            swipeListener.onSwipeLeft(position);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}