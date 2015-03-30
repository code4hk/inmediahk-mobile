package net.inmediahk.reader.Model;

import android.os.Parcel;
import android.os.Parcelable;

import net.inmediahk.reader.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FacebookItem implements Parcelable {
    public static final Parcelable.Creator<FacebookItem> CREATOR = new Parcelable.Creator<FacebookItem>() {
        public FacebookItem createFromParcel(Parcel in) {
            return new FacebookItem(in);
        }

        public FacebookItem[] newArray(int size) {
            return new FacebookItem[size];
        }
    };
    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
    private final static SimpleDateFormat output = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
    public String id, message, picture, link, source, name, description, icon, type, created_time, update_time;
    public From from;

    private FacebookItem(Parcel in) {
        message = in.readString();
        description = in.readString();
        name = in.readString();
        created_time = in.readString();
        link = in.readString();
        type = in.readString();
    }

    public boolean isSelfPost() {
        return from.getId().equals(Settings.FACEBOOK_ID);
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getPicture() {
        return picture;
    }

    public String getLink() {
        if (this.link.indexOf("https://www.facebook.com") > 0) return link;
        final String sid = id.replace(Settings.FACEBOOK_ID + "_", "");
        this.link = "https://www.facebook.com/permalink.php?id=" + Settings.FACEBOOK_ID + "&story_fbid=" + sid;
        return this.link;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public String getCreated_time() {
        return praseDate(created_time);
    }

    public String getUpdate_time() {
        return praseDate(update_time);
    }

    private String praseDate(String s) {
        try {
            final Date date = formatter.parse(s);
            return output.format(date);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(created_time);
        dest.writeString(link);
        dest.writeString(type);
    }

    public class From {
        private String id;

        public String getId() {
            return id;
        }
    }
}
