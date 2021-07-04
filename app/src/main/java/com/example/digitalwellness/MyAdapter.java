package com.example.digitalwellness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class MyAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<MyModel> modeArrayList;

    public MyAdapter(Context context, ArrayList<MyModel> modeArrayList) {
        this.context = context;
        this.modeArrayList = modeArrayList;
    }


    @Override
    public int getCount() {
        return modeArrayList.size(); //returns size of items in list
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //inflat layout card_item.xml
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);

        //init uid views from card_item.xml'

        ImageView bannerTV = view.findViewById(R.id.progressIv);
        TextView titleTV = view.findViewById(R.id.titleTv);
        TextView dataTV = view.findViewById(R.id.dataTv);
        TextView progressTV = view.findViewById(R.id.progressTv);
        TextView pageTV = view.findViewById(R.id.pageTV);

        //get data
        MyModel model = modeArrayList.get(position);
        String title = model.getTitle();
        String data = model.getData();
        String progress = model.getProgress();
        String page = model.getPageNum();
        int image = model.getImage();


        //set data
        bannerTV.setImageResource(image);
        titleTV.setText(title);
        dataTV.setText(data);
        progressTV.setText(progress);
        pageTV.setText(page);


        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
            }


        });

        container.addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        //super.destroyItem(container, position, object);
    }
}
