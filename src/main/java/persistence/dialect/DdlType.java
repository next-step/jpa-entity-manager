package persistence.dialect;

public class DdlType {
    private final String typeNamePattern;

    public DdlType(String typeNamePattern) {
        this.typeNamePattern = typeNamePattern;
    }

    public String getTypeName(Long size) {
        return replace(typeNamePattern, "$l", size.toString());
    }

    public static String replace(String template, String placeholder, String replacement) {
        if (template == null) {
            return null;
        }
        int loc = template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }

        return template.substring(0, loc) + replacement + template.substring(loc + placeholder.length());
    }
}
