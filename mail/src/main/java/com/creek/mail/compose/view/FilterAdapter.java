package com.creek.mail.compose.view;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;


import com.creek.database.DBManager;
import com.creek.common.MailContact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public abstract class FilterAdapter<T> extends ArrayAdapter<T> {
    private List<T> originalObjects;
    private Filter filter;
    private Context context;

    public FilterAdapter(Context context, int resource, T[] objects) {
        this(context, resource, 0, (T[]) objects);
    }

    public FilterAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        this(context, resource, textViewResourceId, (List) (new ArrayList(Arrays.asList(objects))));
    }

    public FilterAdapter(Context context, int resource, List<T> objects) {
        this(context, resource, 0, (List) objects);
    }

    public FilterAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, new ArrayList(objects));
        this.originalObjects = objects;
        this.context = context;
    }

    public void notifyDataSetChanged() {
        ((AppFilter) this.getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetChanged();
    }

    public void notifyDataSetInvalidated() {
        ((AppFilter) this.getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetInvalidated();
    }

    public Filter getFilter() {
        if (this.filter == null) {
            this.filter = new AppFilter(this.originalObjects);
        }

        return this.filter;
    }

    List<MailContact> searchResults = new ArrayList<>();
    List<MailContact> localContacts = new ArrayList<>();

    protected abstract boolean keepObject(T var1, String var2);

    private class AppFilter extends Filter {
        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> var1) {
            this.setSourceObjects(var1);
            List<MailContact> contacts = DBManager.selectMailContactByKey("");
            localContacts = contacts;
            searchResults = contacts;
        }

        public void setSourceObjects(List<T> objects) {
            synchronized (this) {
                this.sourceObjects = new ArrayList(objects);
            }
        }


        protected FilterResults performFiltering(CharSequence chars) {

            FilterResults result = new FilterResults();

            if (chars != null && chars.length() > 0) {
                String mask = chars.toString();
                ArrayList<T> keptObjects = new ArrayList();
                Iterator var5 = this.sourceObjects.iterator();

                while (var5.hasNext()) {
                    T object = (T) var5.next();
                    if (FilterAdapter.this.keepObject(object, mask)) {
                        keptObjects.add(object);
                    }
                }


                result.count = keptObjects.size();
                result.values = keptObjects;
            } else {
                result.values = this.sourceObjects;
                result.count = this.sourceObjects.size();
            }

            return result;
        }

        private boolean isContactContain(String email,String dept) {
            List<MailContact> contacts= (List<MailContact>) sourceObjects;
            for (MailContact contact:contacts){
                if (contact.getEmail_addr().equals(email)){
                    contact.setDepartment(dept);
                    return true;
                }
            }
            return false;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            FilterAdapter.this.clear();
            if (results.count > 0) {
                FilterAdapter.this.addAll((Collection) results.values);
                FilterAdapter.this.notifyDataSetChanged();
            } else {
                FilterAdapter.this.notifyDataSetInvalidated();
            }

        }
    }
}
