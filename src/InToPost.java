import java.util.*;

public class InToPost {
    private StackX[] theStack;
    private List<List<Token>> tokens;
    private List<List<Token>> tokensPostfix;

    public InToPost(List<List<Token>> tokens) {
        int stackSize = tokens.size();
        theStack = new StackX[stackSize];
        this.tokens = tokens;
        tokensPostfix = new ArrayList<>();
    }

    public List<List<Token>> doTrans() {
        for (int i = 0; i < tokens.size(); i++) {
            theStack[i] = new StackX(tokens.get(i).size());
            List<Token> aux = new ArrayList<>();
            for (int j = 0; j < tokens.get(i).size(); j++) {
                Token tkn = tokens.get(i).get(j);
                theStack[i].displayStack("For " + tkn.getValor() + "");
                switch (tkn.getValor()) {
                    case "+":
                    case "-":
                        gotOper(tkn, 1, i, aux);
                        break;
                    case "*":
                        theStack[i].push(tkn);
                        break;
                    case "/":
                        theStack[i].push(tkn);
                        break;
                    default:
                        aux.add(tkn);
                        break;
                }
            }

            while (!theStack[i].isEmpty()) {
                theStack[i].displayStack("While ");
                aux.add(theStack[i].pop());
            }
            tokensPostfix.add(aux);
            theStack[i].displayStack("End ");
        }
        imprimir();
        return tokensPostfix;
    }

    public void gotOper(Token opThis, int prec1, int index, List<Token> aux) {
        while (!theStack[index].isEmpty()) {
            Token opTop = theStack[index].pop();
            if (opTop.getValor() == "(") {
                theStack[index].push(opTop);
                break;
            } else {
                int prec2;
                if (opTop.getValor() == "+" || opTop.getValor() == "-")
                    prec2 = 1;
                else
                    prec2 = 2;
                if (prec2 < prec1) {
                    theStack[index].push(opTop);
                    break;
                } else {
                    aux.add(opTop);
                }
            }
        }
        theStack[index].push(opThis);
    }

    private void imprimir() {

        for (int i = 0; i < tokensPostfix.size(); i++) {
            for (int j = 0; j < tokensPostfix.get(i).size(); j++) {
                System.out.print(tokensPostfix.get(i).get(j).getValor() + " ");
            }
            System.out.println();
        }
    }
}
