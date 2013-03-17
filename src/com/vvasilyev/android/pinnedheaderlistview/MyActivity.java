package com.vvasilyev.android.pinnedheaderlistview;

import android.app.Activity;
import android.os.Bundle;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href='mailto:dewdroid@gmail.com'>Vladimir Vasilyev</a>
 * @version 15.03.13
 */
public class MyActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Map<String, Integer> viewIdMapping = new HashMap<String, Integer>();
        viewIdMapping.put("title", R.id.text1);
        viewIdMapping.put("time", R.id.text2);
        Map<String, Format> formatMapping = new HashMap<String, Format>();
        formatMapping.put("time", new SimpleDateFormat("HH:mm")) ;
        SectionAdapter<Model> adapter = new SectionAdapter<Model>(this,
                "category", R.layout.header_item, R.id.header_text, R.layout.list_item,
                viewIdMapping, formatMapping);
        PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        List<Model> list = new ArrayList<Model>();
        for (int i = 10, k = 1; i < 100; i++) {
            list.add(new Model(Integer.toString(i), new Category(Integer.toString(k))));
            if (i == 12 || i == 14 || i ==18 || i == 21 || i == 24) {
                k++;
            }
        }
        adapter.addAll(list);
    }


    class Model {

        public String title;

        public Date time;

        public Category category;

        Model(String title, Category category) {
            this.title = title;
            this.category = category;
            time = new Date();
        }
    }

    class Category {

        public String title;

        Category(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
