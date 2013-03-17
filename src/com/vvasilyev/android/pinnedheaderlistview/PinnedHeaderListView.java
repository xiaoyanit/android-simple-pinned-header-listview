package com.vvasilyev.android.pinnedheaderlistview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

/**
 * @author <a href='mailto:dewdroid@gmail.com'>Vladimir Vasilyev</a>
 * @version 15.03.13
 */
public class PinnedHeaderListView extends LinearLayout{

    private final ListView listView;
    private final ScrollView headerView;
    private final LinearLayout headerLayout;
    private Activity context;
    private TextView view2;
    private TextView view1;
    private int dividerHeight = 1;

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.pinned_header_listview, this);

        headerView = (ScrollView) findViewById(R.id.headerView);
        headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
        listView = (ListView) findViewById(R.id.internal_listView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SectionAdapter.Item item = (SectionAdapter.Item) view.getItemAtPosition(firstVisibleItem);
                if (item == null) {
                    return;
                }
                if (item instanceof  SectionAdapter.ListItem) {
                    headerView.scrollTo(0, 0);
                    view2.setText(getAdapter().getHeader((SectionAdapter.ListItem) item));
                }  else {
                    View child = item.getView(null, null);
                    if (child != null) {
                        float offset = - child.getY();
                        headerView.scrollTo(0, (int) offset);
                        view1.setText(((SectionAdapter.HeaderItem) item).getObject());
                        view2.setText(getAdapter().getHeader(
                                (SectionAdapter.ListItem) view.getItemAtPosition(firstVisibleItem - 1)));
                    }
                }

            }
        });
    }

    public void setAdapter(SectionAdapter adapter) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view =  inflater.inflate(adapter.getHeaderLayoutId(), headerLayout, false);
         view2 = (TextView) view.findViewById(adapter.getHeaderTextViewId());
        headerLayout.addView(view);

        View divider = new View(context);
        divider.setBackgroundColor(android.R.color.white);
        divider.setMinimumHeight(dividerHeight);
        headerLayout.addView(divider);

        view =  inflater.inflate(adapter.getHeaderLayoutId(), headerLayout, false);
        view1 = (TextView) view.findViewById(adapter.getHeaderTextViewId());
        headerLayout.addView(view);

        headerView.getLayoutParams().height = view.getLayoutParams().height + dividerHeight;

        listView.setAdapter(adapter);
    }

    public SectionAdapter getAdapter() {
        return (SectionAdapter) listView.getAdapter();
    }

}
