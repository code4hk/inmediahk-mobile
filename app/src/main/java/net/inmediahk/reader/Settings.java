package net.inmediahk.reader;

import java.util.Arrays;
import java.util.List;

public class Settings {
    // <a href="http://www.inmediahk.net/" title="">主頁</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/5030" title="">政經</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/5034" title="">社運</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/5018" title="">媒體</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/513024" title="">保育</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/5027" title="">國際</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/513025" title="">生活</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/5043" title="">動物</a>
    // <a href="http://www.inmediahk.net/taxonomy/term/510975" title="">體育</a>
    public final static String URL_BASE = "http://www.inmediahk.net/";
    public final static int TOTAL_TABS = 9;

    public final static List<Category> CATEGORY_LIST = Arrays.asList(
            new Category("主 頁", -1),
            new Category("政 經", 5030),
            new Category("社 運", 5034),
            new Category("媒 體", 5018),
            new Category("保 育", 513024),
            new Category("國 際", 5027),
            new Category("生 活", 513025),
            new Category("動 物", 5043),
            new Category("體 育", 510975)
            );

    public static class Category {
        String name;
        int id;

        public Category(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getUrl() {
            if (this.id == -1)
                return URL_BASE+"full/feed";
            else
                return URL_BASE+"taxonomy/term/"+id+"/feed";
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}