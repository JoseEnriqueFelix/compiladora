public class Token {
    private String tipoDeToken;
    private String valor; // Este es el que quiero
    private int tokenNum;
    private int tokenNumPr;

    public Token(String tipoDeToken, String valor, int tokenNum) {
        this.tipoDeToken = tipoDeToken;
        this.valor = valor;
        this.tokenNum = tokenNum;
    }

    public String getTipoDeToken() {
        return tipoDeToken;
    }

    public String getValor() {
        return valor;
    }

    public int getTokenNum() {
        return tokenNum;
    }

    public int getTokenNumPr() {
        return tokenNumPr;
    }

    public void setTokenNumPr(int tokenNumPr) {
        this.tokenNumPr = tokenNumPr;
    }

}
