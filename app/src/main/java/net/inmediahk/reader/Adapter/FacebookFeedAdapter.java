package net.inmediahk.reader.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.inmediahk.reader.Model.FacebookItem;
import net.inmediahk.reader.R;

import java.util.ArrayList;

public class FacebookFeedAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private Context mContext = null;

    private ArrayList<FacebookItem> listItem = new ArrayList<>();

    public FacebookFeedAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public void setData(ArrayList<FacebookItem> listItem) {
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return this.listItem.size();
    }

    @Override
    public FacebookItem getItem(int position) {
        return this.listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.listItem.get(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_facebook_list, parent, false);
            holder = new ViewHolder();
            holder.IvImage = (ImageView) convertView.findViewById(R.id.IvImage);
            holder.IvImageOnly = (ImageView) convertView.findViewById(R.id.IvImageOnly);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
            holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            holder.lImage = (LinearLayout) convertView.findViewById(R.id.layoutImage);
            holder.lImageOnly = (LinearLayout) convertView.findViewById(R.id.layoutImageOnly);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(getItem(position).getName())) {
            holder.txtTitle.setText(getItem(position).getName());
            holder.lImage.setVisibility(View.VISIBLE);
            holder.lImageOnly.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(getItem(position).getPicture()))
                Picasso.with(mContext).load(getItem(position).getPicture()).into(holder.IvImage);
        } else {
            holder.txtTitle.setText("");
            holder.lImage.setVisibility(View.GONE);
            holder.lImageOnly.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(getItem(position).getPicture()))
                Picasso.with(mContext).load(getItem(position).getPicture()).into(holder.IvImageOnly);
        }

        if (!TextUtils.isEmpty(getItem(position).getCreated_time()))
            holder.txtDate.setText(getItem(position).getCreated_time());
        else
            holder.txtDate.setText("");

        if (!TextUtils.isEmpty(getItem(position).getMessage()))
            holder.txtMessage.setText(getItem(position).getMessage());
        else
            holder.txtMessage.setText("");

        if (!TextUtils.isEmpty(getItem(position).getDescription())) {
            if (getItem(position).getDescription().length() >= 90)
                holder.txtDesc.setText(getItem(position).getDescription().substring(0, 90) + "...");
            else
                holder.txtDesc.setText(getItem(position).getDescription());
        } else {
            holder.txtDesc.setText("");
            holder.txtDesc.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle, txtDesc, txtDate, txtMessage;
        ImageView IvImage, IvImageOnly;
        LinearLayout lImageOnly, lImage;
    }

//    data = {java.util.ArrayList@831903263360} size = 25
//            [0] = {net.inmediahk.reader.Model.FacebookFeed@831903645760}
//    [1] = {net.inmediahk.reader.Model.FacebookFeed@831903501616}
//    created_time = {java.lang.String@831902827424}"2014-10-19T20:00:01+0000"
//    description = {java.lang.String@831903105760}"連日來，警察毆打示威者的報導、相片、影片等在網上瘋傳。一時間，警隊形象跌至最低點。很多從未參加集會的市民，也按不住怒火，毅然走上街頭。筆者在十月十八日凌晨留守旺角，目睹不少激動的示威者高聲辱罵警員，甚至主動上前挑釁。警民關係越變尖銳，對整個雨傘運動會構成甚麼影響？ 停一停，想一想 回顧初衷，我們站出來，連夜露宿街頭，為的是甚麼？「公民提名」、「真普選」從來是雨傘運動的爭取目標。在義憤填膺的同時，我們更應冷靜地想想，怎樣才能爭取更多的支持和關注，怎樣才能打贏漂亮的一仗。 警察不是外星人，他們跟我們一樣，說...
//    icon = {java.lang.String@831903103536}"https://fbstatic-a.akamaihd.net/rsrc.php/v2/yD/r/aS8ecmYRys0.gif"
//    id = {java.lang.String@831903191320}"200954406608272_759701614066879"
//    link = {java.lang.String@831903167224}"http://www.inmediahk.net/node/1027447"
//    message = {java.lang.String@831903174392}"「我們真正需要的，不是路障，而是民心。民心會向政府還是示威者靠攏，就看接下來大家的心態和行為。」"
//    name = {java.lang.String@831903109272}"越趨激烈的抗爭　該如何應對警察？ | 卡卡卡 | 香港獨立媒體網"
//    picture = {java.lang.String@831903171808}"https://fbexternal-a.akamaihd.net/safe_image.php?d=AQC3udavbjF5XxDP&w=158&h=158&url=http%3A%2F%2Fwww.inmediahk.net%2Ffiles%2Fcolumn_images%2FDSC_0130.JPG"
//    source = null
//    type = {java.lang.String@831903100472}"link"
//    update_time = null
//            [2] = {net.inmediahk.reader.Model.FacebookFeed@831904100176}
//    created_time = {java.lang.String@831902663848}"2014-10-19T19:04:34+0000"
//    description = {java.lang.String@831902685488}"2014年10月20日為中國四中全會日, 香港人如果真的想要真普選, 請曾經在本年7月1日遊行過或沒有遊行過, 為數超過100萬的香港人再次於未來兩天在金鐘出現, 以表示對真普選的決心和誠意, 人大曾經數次說明不能改變但最後都被民意改變, 請展示決心, 不要數十年後被下一代問及當年的18-20日你去了哪裡、為什麼..."
//    icon = {java.lang.String@831902683920}"https://fbstatic-a.akamaihd.net/rsrc.php/v2/yj/r/v2OnaTyTQZE.gif"
//    id = {java.lang.String@831902818888}"200954406608272_10204333347950408"
//    link = {java.lang.String@831902799520}"https://www.youtube.com/watch?v=OofLZpWfd-Y&feature=share"
//    message = {java.lang.String@831902813400}"https://www.youtube.com/watch?v=OofLZpWfd-Y&feature=share"
//    name = {java.lang.String@831902687552}"政界中人: 2014年10月20日決定香港生死存亡"
//    picture = {java.lang.String@831902808136}"https://fbexternal-a.akamaihd.net/safe_image.php?d=AQAWOZwQc4JEj8bI&w=130&h=130&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FOofLZpWfd-Y%2Fhqdefault.jpg"
//    source = {java.lang.String@831902689888}"http://www.youtube.com/v/OofLZpWfd-Y?version=3&autohide=1&autoplay=1"
//    type = {java.lang.String@831902670680}"video"
//    update_time = null
//            [3] = {net.inmediahk.reader.Model.FacebookFeed@831903654368}
//    created_time = {java.lang.String@831902477512}"2014-10-19T19:00:41+0000"
//    description = null
//    icon = {java.lang.String@831902625776}"https://fbstatic-a.akamaihd.net/rsrc.php/v2/yx/r/og8V99JVf8G.gif"
//    id = {java.lang.String@831902652576}"200954406608272_760007754036265"
//    link = {java.lang.String@831902628904}"https://www.facebook.com/inmediahk/photos/a.470741732962870.94741.200954406608272/760006710703036/?type=1&relevant_count=1"
//    message = {java.lang.String@831902640544}"【金鐘現場】(3:00) 又一個晚上了，大家繼續努力，守護香港！"
//    name = null
//    picture = {java.lang.String@831902632408}"https://fbcdn-sphotos-f-a.akamaihd.net/hphotos-ak-xpf1/v/t1.0-9/s130x130/10624919_760006710703036_4714041498787508875_n.jpg?oh=3ac65f1dfaf2a13f5c8d0c0bbf9f01c1&oe=54B14957&__gda__=1420422634_802c6abc7598577e00fc26452db16e19"
//    source = null
//    type = {java.lang.String@831902503512}"photo"
//    update_time = null
}
