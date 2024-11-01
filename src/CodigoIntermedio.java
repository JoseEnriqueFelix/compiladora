public class CodigoIntermedio {
    private static String codigoSectionData;
    private static String codigoSectionCode;

    public static String getSectionData() {
        return codigoSectionData;
    }

    public static void setSectionData(String codigo) {
        CodigoIntermedio.codigoSectionData = codigo;
    }

    public static String getCodigoSectionCode() {
        return codigoSectionCode;
    }

    public static void setCodigoSectionCode(String codigo) {
        CodigoIntermedio.codigoSectionCode = codigo;
    }

    public static String getCodigoIntermedio(){
        return codigoSectionData + codigoSectionCode;
    }
}
