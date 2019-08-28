package com.malangyee.bogoilgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Book> books;
    private LayoutInflater inf;
    private ViewHolder viewHolder;
    private Bitmap bitmap;

    public MyAdapter(Context context, int layout, ArrayList<Book> books) {
        this.context = context;
        this.layout = layout;
        this.books = books;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        bitmap = null;
        if(view == null) {
            view = inf.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.tv_title);
            viewHolder.img = view.findViewById(R.id.iv_poster);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(books.get(i).getTitle());
        if(!books.get(i).getImg().equals("")) {
            Glide.with(context).load(books.get(i).getImg()).into(viewHolder.img);
        }

        return view;

    }

    class ViewHolder{
        public TextView title;
        public ImageView img;
    }
}
