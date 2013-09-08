package name.davidfischer.civilopedia.helpers;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public final class CivilopediaHtmlHelper {
    private static final char DELIMETER = '$';
    private static final String [][] REPLACEMENTS = new String [][] {
        new String [] {"[TAB]", ""},
        new String [] {"[NEWLINE]", "<br />"},
        new String [] {"[COLOR_POSITIVE_TEXT]", "<em class='positive-text'>"},
        new String [] {"[ENDCOLOR]", "</em>"},

        // Icons
        new String [] {"[ICON_FOOD]", "<span class='icon icon-food'></span>"},
        new String [] {"[ICON_PRODUCTION]", "<span class='icon icon-production'></span>"},
        new String [] {"[ICON_GOLD]", "<span class='icon icon-gold'></span>"},
        new String [] {"[ICON_CULTURE]", "<span class='icon icon-culture'></span>"},
        new String [] {"[ICON_RESEARCH]", "<span class='icon icon-research'></span>"},
        new String [] {"[ICON_GOLDEN_AGE]", "<span class='icon icon-golden-age'></span>"},
        new String [] {"[ICON_GREAT_PEOPLE]", "<span class='icon icon-great-people'></span>"},
        new String [] {"[ICON_STRENGTH]", "<span class='icon icon-defense'></span>"},
        new String [] {"[ICON_HAPPINESS_1]", "<span class='icon icon-happiness'></span>"},  // Note name difference
        new String [] {"[ICON_HAPPINESS_4]", "<span class='icon icon-unhappiness'></span>"},  // Note name difference
        new String [] {"[ICON_PEACE]", "<span class='icon icon-faith'></span>"},  // Note name difference
        new String [] {"[ICON_OCCUPIED]", "<span class='icon icon-occupied'></span>"},
        new String [] {"[ICON_DIPLOMAT]", "<span class='icon icon-diplomat'></span>"},
        new String [] {"[ICON_TOURISM]", "<span class='icon icon-tourism'></span>"},
        new String [] {"[ICON_RES_SHEEP]", "<span class='icon icon-sheep'></span>"},
        new String [] {"[ICON_RES_COW]", "<span class='icon icon-cow'></span>"},
        new String [] {"[ICON_RES_HORSE]", "<span class='icon icon-horse'></span>"},
        new String [] {"[ICON_RES_IRON]", "<span class='icon icon-iron'></span>"},
        new String [] {"[ICON_RES_FISH]", "<span class='icon icon-fish'></span>"},
        new String [] {"[ICON_RES_PEARLS]", "<span class='icon icon-pearls'></span>"},
        new String [] {"[ICON_RES_DEER]", "<span class='icon icon-deer'></span>"},
        new String [] {"[ICON_RES_IVORY]", "<span class='icon icon-ivory'></span>"},
        new String [] {"[ICON_RES_FUR]", "<span class='icon icon-fur'></span>"},
        new String [] {"[ICON_RES_TRUFFLES]", "<span class='icon icon-truffles'></span>"},
        new String [] {"[ICON_RES_MARBLE]", "<span class='icon icon-marble'></span>"},
        new String [] {"[ICON_RES_COAL]", "<span class='icon icon-coal'></span>"},
        new String [] {"[ICON_RES_OIL]", "<span class='icon icon-oil'></span>"},
        new String [] {"[ICON_RES_ALUMINUM]", "<span class='icon icon-aluminum'></span>"},
        new String [] {"[ICON_RES_URANIUM]", "<span class='icon icon-uranium'></span>"},
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

        for (int i = 0; i < CivilopediaHtmlHelper.REPLACEMENTS.length; i += 1) {
            searchList[i] = CivilopediaHtmlHelper.REPLACEMENTS[i][0];
            replacementList[i] = CivilopediaHtmlHelper.REPLACEMENTS[i][1];
        }
        return StringUtils.replaceEach(html, searchList, replacementList);
    }
}
