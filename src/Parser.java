import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Parser {

    private SyntaxTree[] sts;

    // Tabla tokens:
    // 1 => IG
    // 2 => SUM
    // 3 => MIN
    // 4 => DIV
    // 5 => MULT
    // 6 => PC
    // 7 => LLAP
    // 8 => LLAC
    // 9 => NUM
    // 10 => PR
    // 11 => ID

    // Tabla valores especiales para palabras claves:
    // 12 => int
    // 13 => boolean
    // 14 => true || false

    public Parser() {
        sts = null;
    }

    public SyntaxTree[] getSyntaxTrees() {
        return sts;
    }

    public boolean parser(ArrayList<Token> tokens) {
        sts = null;
        try {
            if (tokens.get(0).getTokenNum() != 10 || !tokens.get(0).getValor().equals("main"))
                return false;
            if (tokens.get(1).getTokenNum() != 7)
                return false;
            if (tokens.size() == 3 && tokens.get(2).getTokenNum() == 8)
                return true;
            List<List<Token>> statements = armarStatements(tokens.subList(2, tokens.size() - 1));
            CodigoIntermedio codInt = new CodigoIntermedio();
            generarCodigoIntermedio(statements, codInt);
            if (!evaluarStatements(statements))
                return false;
            if (tokens.get(tokens.size() - 1).getTokenNum() != 8)
                return false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void generarCodigoIntermedio(List<List<Token>> statements, CodigoIntermedio codInt) {
        String data = ".data\n";
        String code = ".code\n";
        HashMap<String, Integer> variables = new HashMap<>();
        // .data
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i).get(0).getTokenNumPr() == 12) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() == 11 && statements.get(i).get(2).getTokenNum() == 6) {
                        data += statements.get(i).get(1).getValor() + "    dw  ?\n";
                        variables.put(statements.get(i).get(1).getValor(), null);
                    }
                } else {
                    if (statements.get(i).get(1).getTokenNum() == 11 && statements.get(i).get(2).getTokenNum() == 1
                            && statements.get(i).get(statements.get(i).size() - 1).getTokenNum() == 6
                            && parserLista(statements.get(i).subList(3, statements.get(i).size() - 1))) {
                        if (statements.get(i).subList(3, statements.get(i).size() - 1).size() == 1) {
                            data += statements.get(i).get(1).getValor() + "    dw  "
                                    + statements.get(i).get(3).getValor() + "\n";
                            variables.put(statements.get(i).get(1).getValor(),
                                    Integer.parseInt(statements.get(i).get(3).getValor()));
                        } else {
                            data += statements.get(i).get(1).getValor() + "    dw  ?\n";
                            variables.put(statements.get(i).get(1).getValor(), null);
                        }

                    }
                }
            } else if (statements.get(i).get(0).getTokenNumPr() == 13) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() == 11 && statements.get(i).get(2).getTokenNum() == 6) {
                        data += statements.get(i).get(1).getValor() + "   db  ?\n";
                        variables.put(statements.get(i).get(1).getValor(), null);
                    }
                } else {
                    if (statements.get(i).get(1).getTokenNum() == 11 && statements.get(i).get(2).getTokenNum() == 1
                            && statements.get(i).get(3).getTokenNumPr() == 14
                            && statements.get(i).get(4).getTokenNum() == 6) {
                        int aux;
                        if (statements.get(i).get(3).getValor().equals("false"))
                            aux = 0;
                        else
                            aux = 1;
                        data += statements.get(i).get(1).getValor() + "     db  " + aux + "\n";
                        variables.put(statements.get(i).get(1).getValor(), aux);
                    }
                }
            }
        }
        CodigoIntermedio.setSectionData(data);
        // .code
        // int Id = lista; && id = lista; && id = valBool;
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i).get(0).getTokenNumPr() == 12) {
                if (statements.get(i).get(1).getTokenNum() == 11 && statements.get(i).get(2).getTokenNum() == 1
                        && statements.get(i).get(statements.get(i).size() - 1).getTokenNum() == 6
                        && parserLista(statements.get(i).subList(3, statements.get(i).size() - 1))) {
                    if (statements.get(i).subList(3, statements.get(i).size() - 1).size() == 1) {
                        if (variables.containsKey(statements.get(i).get(1).getValor())
                                && variables.get(statements.get(i).get(1).getValor()) == Integer
                                        .parseInt(statements.get(i).get(3).getValor())) {
                            continue;
                        }
                        code += "MOV    " + statements.get(i).get(1).getValor() + ",    "
                                + Integer.parseInt(statements.get(i).get(3).getValor()) + "\n";
                        continue;
                    } else {
                        // TODO
                    }
                }
            }
            if (statements.get(i).get(0).getTokenNum() == 11) {
                if (statements.get(i).size() == 4) {
                    if (statements.get(i).get(1).getTokenNum() == 1 && statements.get(i).get(3).getTokenNum() == 6) {
                        if (parserLista(statements.get(i).subList(2, 3))) {
                            code += "MOV    " + statements.get(i).get(0).getValor() + ",    "
                                    + Integer.parseInt(statements.get(i).get(2).getValor()) + "\n";
                            continue;
                        } else {
                            int aux;
                            if (statements.get(i).get(2).getValor().equals("false"))
                                aux = 0;
                            else
                                aux = 1;
                            code += "MOV    " + statements.get(i).get(0).getValor() + ",    "
                                    + aux + "\n";
                            continue;
                        }
                    }
                }
                else {
                    //TODO
                }
            }
        }
        CodigoIntermedio.setCodigoSectionCode(code);
        System.out.println(CodigoIntermedio.getCodigoIntermedio());
    }

    private boolean evaluarStatements(List<List<Token>> statements) {
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i).get(0).getTokenNumPr() == 12) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 6)
                        return false;
                } else {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 1)
                        return false;
                    if (!parserLista(statements.get(i).subList(3, statements.get(i).size() - 1)))
                        return false;
                    if (statements.get(i).get(statements.get(i).size() - 1).getTokenNum() != 6)
                        return false;
                }
            } else if (statements.get(i).get(0).getTokenNumPr() == 13) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 6)
                        return false;
                } else {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 1
                            || statements.get(i).get(3).getTokenNumPr() != 14)
                        return false;
                    if (statements.get(i).get(statements.get(i).size() - 1).getTokenNum() != 6
                            || statements.get(i).size() != 5)
                        return false;
                }
            } else if (statements.get(i).get(0).getTokenNum() == 11) {
                if (statements.get(i).get(1).getTokenNum() == 1) {
                    if (!parserLista(statements.get(i).subList(2, statements.get(i).size() - 1))) {
                        if (statements.get(i).get(2).getTokenNumPr() != 14
                                || statements.get(i).get(statements.get(i).size() - 1).getTokenNum() != 6
                                || statements.get(i).size() != 4)
                            return false;
                    }
                    if (statements.get(i).get(statements.get(i).size() - 1).getTokenNum() != 6)
                        return false;
                } else
                    return false;
            } else
                return false;
        }
        sts = new SyntaxTree[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            sts[i] = new SyntaxTree(statements.get(i).size());
            for (int j = 0; j < statements.get(i).size(); j++)
                sts[i].insertar(statements.get(i).get(j));
        }

        return true;
    }

    private void generarSyntaxTree(List<Token> statement) {
    }

    private List<List<Token>> armarStatements(List<Token> tokens) {
        int pos = 0;
        List<List<Token>> retorno = new ArrayList<>();
        List<Token> aux = new ArrayList<>();
        while (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (curr.getTokenNum() == 6) {
                aux.add(curr);
                retorno.add(aux);
                aux = new ArrayList<>();
                pos++;
                continue;
            }
            if (curr.getTokenNum() == 10) {
                if (tokens.get(pos).getValor().equals("int")) {
                    curr.setTokenNumPr(12);
                    aux.add(curr);
                } else if (tokens.get(pos).getValor().equals("boolean")) {
                    curr.setTokenNumPr(13);
                    aux.add(curr);
                } else if (tokens.get(pos).getValor().equals("true") || tokens.get(pos).getValor().equals("false")) {
                    curr.setTokenNumPr(14);
                    aux.add(curr);
                } else { // o sea en el caso que la palabra reservada sea main
                    curr.setTokenNumPr(-1);
                    aux.add(curr);
                }
            } else
                aux.add(curr);
            pos++;
        }
        if (tokens.get(--pos).getTokenNum() != 6)
            retorno.add(aux);
        return retorno;
    }

    // lista oper nums | lista oper id | id | nums
    private boolean parserLista(List<Token> arrTokens) {
        if (arrTokens.size() == 1) {
            if (arrTokens.get(0).getTokenNum() == 9 || arrTokens.get(0).getTokenNum() == 11)
                return true;
            else
                return false;
        }
        if (arrTokens.size() < 3)
            return false;
        if (!parserLista(arrTokens.subList(0, arrTokens.size() - 2)))
            return false;
        if (arrTokens.get(arrTokens.size() - 2).getTokenNum() < 2
                || arrTokens.get(arrTokens.size() - 2).getTokenNum() > 5)
            return false;
        if (arrTokens.get(arrTokens.size() - 1).getTokenNum() != 9
                && arrTokens.get(arrTokens.size() - 1).getTokenNum() != 11)
            return false;
        return true;
    }

    private int obtenerNumero(List<Token> arrTokens) {
        StringBuilder sb = new StringBuilder();
        for (Token token : arrTokens) {
            sb.append(token.getValor()).append(" ");
        }

        String expression = sb.toString().trim();

        return (int) eval(expression);
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ')
                    nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            // | functionName `(` expression `)` | functionName factor
            // | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return +parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')'))
                        throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.')
                        nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z')
                        nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')'))
                            throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt"))
                        x = Math.sqrt(x);
                    else if (func.equals("sin"))
                        x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos"))
                        x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan"))
                        x = Math.tan(Math.toRadians(x));
                    else
                        throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^'))
                    x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
