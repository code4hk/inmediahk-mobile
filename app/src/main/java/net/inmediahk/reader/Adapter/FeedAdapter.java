package net.inmediahk.reader.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.R;

import java.util.ArrayList;

public class FeedAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private Context mContext = null;

    private ArrayList<FeedItem> listItem = new ArrayList<>();

    public FeedAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public void setData (ArrayList<FeedItem> listItem) {
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return this.listItem.size();
    }

    @Override
    public FeedItem getItem(int position) {
        return this.listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtTitle.setText(getItem(position).getTitle());
        holder.txtDate.setText(getItem(position).getDate());
        holder.txtDesc.setText(getItem(position).getDesc());
        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle, txtDesc, txtDate;
    }
}
