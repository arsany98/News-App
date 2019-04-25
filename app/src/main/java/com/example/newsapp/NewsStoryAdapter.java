package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsStoryAdapter extends ArrayAdapter<NewsStory> {
    Context mContext;
    List<NewsStory> mNewsStories;
    public NewsStoryAdapter(Context context, int resource, List<NewsStory> objects) {
        super(context, resource, objects);
        mContext = context;
        mNewsStories = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        NewsStory newsStory = mNewsStories.get(position);

        TextView sectionNameTextView = convertView.findViewById(R.id.section_name_text_view);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);
        TextView authorNameTextView = convertView.findViewById(R.id.author_name_text_view);

        sectionNameTextView.setText(newsStory.getmSectionName());
        titleTextView.setText(newsStory.getmTitle());
        dateTextView.setText(newsStory.getmPublicationDate());
        authorNameTextView.setText(newsStory.getmAuthorName());

        return convertView;
    }
}
