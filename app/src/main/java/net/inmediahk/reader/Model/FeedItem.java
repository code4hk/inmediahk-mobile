package net.inmediahk.reader.Model;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedItem {
    private String mTitle = "", mLink = "", mDate = "", mCreator = "", mDesc = "", mContent = "", mImageUrl = "";
    private int mId;
    private final static SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private final static SimpleDateFormat output = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
    private final Pattern p = Pattern.compile("<img class=\\\"imagefield imagefield-field_image\\\"[^>]+src=\\\"([^\\\">]+)[^>]+>");

    public void recode() {

        mContent = mContent.replaceAll("<p>&nbsp;</p>", "");
        mContent =
        mContent.replaceAll("<iframe[^>]+src=\"//www.youtube.com/embed/([^\">]+)[^>]+></iframe>", "<a href=\"http://www.youtube.com/watch?v=$1\">http://www.youtube.com/watch?v=$1</a>");
        // mDesc = mDesc.replaceAll("\\<object(.*)youtube.com/v/(.*)\"\\>\\</embed\\>\\</object\\>",
        // "<a href=\"http://www.youtube.com/watch?v=$2\">http://www.youtube.com/watch?v=$2</a>");
        // mDesc =
        // mDesc.replaceAll("((?=\\<a href).*(?<=\\</a\\>))","<div style=\"width:100%;overflow-x:scroll;\">$1</div>");
        // mDesc =
        // mDesc.replaceAll("\\<img class=\".*\" title=\"(.*)\" src=\"(.*)\" alt=\"\" width=\"(.*)\" height=\"(.*)\" /\\>","\\<img title=\"$1\" src=\"$2\" width=\"100%\" /\\>");
        // mDesc = mDesc.replaceAll("<img class=\\\"imagefield imagefield-field_image\\\"[^>]+src=\\\"([^\\\">]+)[^>]+>", "\\<img src=\"$1\" width=\"100%\" /\\>");

        final Matcher m = p.matcher(mContent);
        if (m.find())
            mImageUrl = m.group(1);

        mContent = mContent.replaceAll("<img class=\\\"imagefield imagefield-field_image\\\"[^>]+src=\\\"([^\\\">]+)[^>]+>", "");

    }

    public void setData(String Title, String Link, String Date, String Creator, String Desc) {
        mTitle = Title;
        mLink = Link;
        mDate = praseDate(Date);
        mCreator = Creator;
        mDesc = Jsoup.parse(Desc).text().substring(0,90)+"...";
        mContent = Desc;
        recode();
    }

    public int getId() {
    return mId;
    }

    public String[] getData() {
    String[] a = {mTitle, mLink, mDate, mCreator, mDesc};
    return a;
    }

    public String getTitle() {
    return mTitle;
    }

    public String getLink() {
    return mLink;
    }

    public String getDate() {
    return mDate;
    }

    public String getCreator() {
    return mCreator;
    }

    public String getDesc() {
    return mDesc;
    }
    public String getContent() {
        return mContent;
    }

    public void setId(int s) {
    this.mId = s;
    }

    public void setTitle(String s) {
    this.mTitle += s;
    }

    public void setLink(String s) {
    this.mLink = s;
    }

    public void setDate(String s) {
    this.mDate = praseDate(s);
  }

    public String getImageUrl() {
        return mImageUrl;
    }

    private String praseDate(String s) {
    try {
      final Date date = (Date) formatter.parse(s);
      return output.format(date);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setCreator(String s) {
    this.mCreator = s;
  }

  public void setDesc(String s) {
    this.mDesc += s;
  }
}
