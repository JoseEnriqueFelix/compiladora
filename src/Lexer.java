import java.util.ArrayList;

public class Lexer {
    private String[] prs = { "main", "int", "boolean", "true", "false" };
    private ArrayList<Token> tokens;
    private String currentString;
    private String digitos = "0123456789";
    private String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public Lexer() {
        currentString = "";
        tokens = new ArrayList<>();
    }

    public ArrayList<Token> analizadorLexico(String s) {
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar == ' ' || currChar == '\n') {
                continue;
            }
            if (currChar == '+') {
                tokens.add(new Token("SUM", ""+currChar, 2));
                continue;
            }
            if (currChar == '=') {
                tokens.add(new Token("IG", ""+currChar, 1));
                continue;
            }
            if (currChar == '-') {
                tokens.add(new Token("MIN", ""+currChar, 3));
                continue;
            }
            if (currChar == '/') {
                tokens.add(new Token("DIV", ""+currChar, 4));
                continue;
            }
            if (currChar == '*') {
                tokens.add(new Token("MULT", ""+currChar, 5));
                continue;
            }
            if (currChar == ';') {
                tokens.add(new Token("PC", ""+currChar, 6));
                continue;
            }
            if (currChar == '{') {
                tokens.add(new Token("LLAP", ""+currChar, 7));
                continue;
            }
            if (currChar == '}') {
                tokens.add(new Token("LLAC", ""+currChar, 8));
                continue;
            }
            if (currChar == '#') {
                while (i < s.length() && currChar != '\n') {
                    i++;
                    if (i < s.length()) {
                        currChar = s.charAt(i);
                    }
                }
                i--;
                continue;
            }
            if (digitos.indexOf(currChar) != -1) {
                StringBuilder currentString = new StringBuilder();
                while (i < s.length() && digitos.indexOf(currChar) != -1) {
                    currentString.append(currChar);
                    i++;
                    if (i < s.length()) {
                        currChar = s.charAt(i);
                    }
                }
                tokens.add(new Token("NUM", currentString.toString(), 9));
                i--;
                continue;
            }
            if (letras.indexOf(currChar) != -1) {
                StringBuilder currentString = new StringBuilder();
                while (i < s.length() && (digitos.indexOf(currChar) != -1 || letras.indexOf(currChar) != -1)) {
                    currentString.append(currChar);
                    i++;
                    if (i < s.length()) {
                        currChar = s.charAt(i);
                    }
                }
                i--;
                if (esPalabraReservada(currentString.toString())) {
                    tokens.add(new Token("PR", currentString.toString(), 10));
                    continue;
                }
                tokens.add(new Token("ID", currentString.toString(), 11));
                continue;
            }
            else{
                tokens.add(new Token("INVALIDO", ""+currChar, -1));
                continue;
            }

        }
        return tokens;
    }

    private boolean esPalabraReservada(String s) {
        for (String pr : prs) {
            if (pr.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
}
