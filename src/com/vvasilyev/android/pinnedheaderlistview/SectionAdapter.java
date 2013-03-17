package com.vvasilyev.android.pinnedheaderlistview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.Format;
import java.util.*;

/**
 * @author <a href='mailto:dewdroid@gmail.com'>Vladimir Vasilyev</a>
 * @version 15.03.13
 */
public class SectionAdapter<T> extends BaseAdapter {

    private final Activity context;
    private String headerProperty;
    List<Item> items = new ArrayList<Item>();
    private int headerLayoutId;
    private int headerTextViewId;
    private int listItemLayoutId;
    private Map<String, Integer> propertyToViewIdMapping;
    private Map<String, Format> propertyToFormatMapping;

    public SectionAdapter(Activity context, String headerProperty, int headerLayoutId, int headerTextViewId,
                          int listItemLayoutId, Map<String, Integer> propertyToViewIdMapping,
                          Map<String, Format> propertyToFormatMapping) {
        this.context = context;
        this.headerProperty = headerProperty;
        this.headerLayoutId = headerLayoutId;
        this.headerTextViewId = headerTextViewId;
        this.listItemLayoutId = listItemLayoutId;
        this.propertyToViewIdMapping = propertyToViewIdMapping;
        this.propertyToFormatMapping = propertyToFormatMapping;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return position < getCount() ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = items.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        return item.getView(inflater, parent);
    }

    public void addAll(List<T> list) {
        items.clear();
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                return Utils.getValue(lhs, headerProperty).toString().compareTo(Utils.getValue(rhs, headerProperty).toString());
            }
        });

        for (int i = 0; i < list.size(); i++) {
            if (i != 0 && Utils.getValue(list.get(i), headerProperty).toString()
                    .compareTo(Utils.getValue(list.get(i - 1), headerProperty).toString()) != 0) {
                HeaderItem item =
                        new HeaderItem(
                                Utils.getValue(list.get(i), headerProperty).toString(),
                                headerLayoutId,
                                headerTextViewId);
                items.add(item);
            }
            items.add(new ListItem<T>(list.get(i), listItemLayoutId, propertyToViewIdMapping, propertyToFormatMapping));
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public String getHeader(ListItem item) {
        return Utils.getStringValue(item.getObject(), headerProperty);
    }

    public int getHeaderLayoutId() {
        return headerLayoutId;
    }

    public int getHeaderTextViewId() {
        return headerTextViewId;
    }

    public static interface Item<K> {

        View getView(LayoutInflater inflater, ViewGroup parent);

        K getObject();

    }

    public static class HeaderItem implements Item<String> {

        private String text;
        private int headerLayoutId;
        private int headerTextViewId;
        private View view;

        public HeaderItem(String text, int headerLayoutId, int headerTextViewId) {
            this.text = text;
            this.headerLayoutId = headerLayoutId;
            this.headerTextViewId = headerTextViewId;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(headerLayoutId, parent, false);
                ((TextView) view.findViewById(headerTextViewId)).setText(text);
            }
            return view;
        }

        @Override
        public String getObject() {
            return text;
        }
    }

    public static class ListItem<T> implements Item<T> {

        private T object;
        private int listItemLayoutId;
        private Map<String, Integer> propertyToViewIdMapping;
        private Map<String, Format> propertyToFormatMapping;

        public ListItem(T object, int listItemLayoutId,
                        Map<String, Integer> propertyToViewIdMapping,
                        Map<String, Format> propertyToFormatMapping) {
            this.object = object;
            this.listItemLayoutId = listItemLayoutId;
            this.propertyToViewIdMapping = propertyToViewIdMapping;
            this.propertyToFormatMapping = propertyToFormatMapping;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            View row = inflater.inflate(listItemLayoutId, parent, false);

            for (String key : propertyToViewIdMapping.keySet()) {
                ((TextView) row.findViewById(propertyToViewIdMapping.get(key)))
                        .setText(getStringValue(object, key));
            }
            return row;
        }

        private String getStringValue(Object object, String propertyExpression) {
            Object propertyValue = Utils.getValue(object, propertyExpression);
            String value = String.valueOf(propertyValue);
            Format format = propertyToFormatMapping.get(propertyExpression);
            if (format != null) {
                value = format.format(propertyValue);
            }
            return value;
        }

        @Override
        public T getObject() {
            return object;
        }
    }

    private static class Utils {

        protected Utils() {
        }

        public static Object getValue(Object object, String propertyExpr) {
            Object value = null;
            try {
                Field f  = object.getClass().getDeclaredField(propertyExpr);
                try {
                    f.setAccessible(true);
                    value = f.get(object);
                } catch (IllegalAccessException e) {
                    // do nothing
                } finally {
                    f.setAccessible(false);
                }
            } catch (NoSuchFieldException e) {
                // do nothing
            }
            return value;
        }

        public static String getStringValue(Object object, String propertyExpr) {
            return String.valueOf(getValue(object, propertyExpr));
        }
    }
}
