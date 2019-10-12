package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.R;

public class PoetSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    List<GanjoorPoet> poets;
    Context context;

    public PoetSpinnerAdapter(Context context, List<GanjoorPoet> poetList) {

        this.context = context;
        this.poets = poetList;
    }

    @Override
    public int getCount() {
        return this.poets.size();
    }

    @Override
    public GanjoorPoet getItem(int position) {
        return poets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.poet_spiner_textview, null);
        TextView textView = view.findViewById(R.id.main);
        textView.setText(getItem(position)._Name);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view;
        view = View.inflate(context, R.layout.poet_spiner_item, null);
        final TextView textView = view.findViewById(R.id.dropdown);
        textView.setText(getItem(position)._Name);
        return view;
    }


}
