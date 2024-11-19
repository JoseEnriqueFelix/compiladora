import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Parser {

    private SyntaxTree[] sts;
    private List<List<Token>> operacionesListas;
    private List<List<Token>> statements;

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

    public List<List<Token>> getStatements() {
        return statements;
    }

    public Parser() {
        sts = null;
        operacionesListas = new ArrayList<List<Token>>();
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
            statements = armarStatements(tokens.subList(2, tokens.size() - 1));
            // CodigoIntermedio codInt = new CodigoIntermedio();
            // generarCodigoIntermedio(statements, codInt);
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

    private boolean evaluarStatements(List<List<Token>> statements) {
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i).get(0).getTokenNumPr() == 12) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 6)
                        return false;
                } else {
                    return false;
                }
            } else if (statements.get(i).get(0).getTokenNumPr() == 13) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() != 11 || statements.get(i).get(2).getTokenNum() != 6)
                        return false;
                } else {
                    return false;
                }
            } else if (statements.get(i).get(0).getTokenNum() == 11) {
                if (statements.get(i).get(1).getTokenNum() == 1) {
                    if (!parserLista(statements.get(i).subList(2, statements.get(i).size() - 1))) {
                        if (statements.get(i).get(2).getTokenNumPr() != 14
                                || statements.get(i).get(statements.get(i).size() - 1).getTokenNum() != 6
                                || statements.get(i).size() != 4)
                            return false;
                    } else {
                        operacionesListas.add(statements.get(i).subList(2, statements.get(i).size() - 1));
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

    public List<List<Token>> getOperacionesListas() {
        return operacionesListas;
    }

    public void vaciaOperacionesListas() {
        operacionesListas = new ArrayList<List<Token>>();
    }

    public SyntaxTree[] getSts() {
        return sts;
    }

}
