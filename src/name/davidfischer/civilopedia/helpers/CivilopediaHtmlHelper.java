package name.davidfischer.civilopedia.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public final class CivilopediaHtmlHelper {
    private static final char DELIMETER = '$';
    private static final String [][] REPLACEMENTS = new String [][] {
        new String [] {"[TAB]", ""},
        new String [] {"[NEWLINE]", "<br />"},
        new String [] {"[COLOR_POSITIVE_TEXT]", "<em class='COLOR_POSITIVE_TEXT'>"},
        new String [] {"[COLOR_CYAN]", "<em class='COLOR_CYAN'>"},
        new String [] {"[ENDCOLOR]", "</em>"},
    };

    private CivilopediaHtmlHelper() {
        // Intentionally left empty
    }

    /**
     * Render the passed HTML `template` with the `params`.
     *
     * @param template the HTML to escape with params
     * @param params key/value pairs to apply HTML escaping
     * @return the escaped HTML output
     */
    public static String format(String template, HashMap<String, String> params) {
        String [] searchList = new String [params.size()];
        String key, val;
        String [] replacementList = new String [params.size()];

        if (null != params) {
            int index = 0;
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                val = CivilopediaHtmlHelper.civilopediaFormatter(StringEscapeUtils.escapeHtml4(params.get(key)));
                searchList[index] = DELIMETER + key + DELIMETER;
                replacementList[index] = val;
                index += 1;
            }
        }
        return StringUtils.replaceEach(template, searchList, replacementList);
    }

    /**
     * Should be run after HTML escaping.
     * @param html
     * @return a copy of `html` with Civilopedia specific replacements performed
     */
    public static String civilopediaFormatter(String html) {
        // This method could be more performant by pre-allocating two
        //  arrays since they never change, but then the code is harder
        //  to maintain.
        String [] searchList = new String [REPLACEMENTS.length];
        String [] replacementList = new String [REPLACEMENTS.length];
        String result = html;

        if (null == html) {
            return "";
        }

        // Replace icons
        result = result.replaceAll(Pattern.quote("[") + "(ICON_[A-Z0-9_]+)" + Pattern.quote("]"), "<span class='icon $1'></span>");
        for (int i = 0; i < CivilopediaHtmlHelper.REPLACEMENTS.length; i += 1) {
            searchList[i] = CivilopediaHtmlHelper.REPLACEMENTS[i][0];
            replacementList[i] = CivilopediaHtmlHelper.REPLACEMENTS[i][1];
        }
        return StringUtils.replaceEach(result, searchList, replacementList);
    }
}
