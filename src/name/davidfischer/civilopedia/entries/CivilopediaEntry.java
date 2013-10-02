package name.davidfischer.civilopedia.entries;


public abstract class CivilopediaEntry {
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String GROUP = "group";

    public CivilopediaEntry() {
    }

    public abstract String getKey();
    public abstract String getName();
    public abstract String getGroup();
}
