import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParsePost {
    private Stack<String> theStack;
    private List<Token> input;
    private final static String[] REGISTROS = { "AX", "BX", "CX", "DX", "SP", "BP", "SI", "DI" };
    private boolean[] registroDisponible = { true, true, true, true, true, true, true, true };
    private List<String> codigoIntermedio;
    private String memoria;

    public ParsePost(List<Token> s, String memoria) {
        input = s;
        codigoIntermedio = new ArrayList<>();
        this.memoria = memoria;
        // AX reservado para multiplicación y división
        registroDisponible[0] = false;
    }

    public List<String> doParse() {
        theStack = new Stack<>();
        Token token;
        String registroResultado;
        String reg1 = "", reg2 = "";

        for (int j = 0; j < input.size(); j++) {
            token = input.get(j);
            if (esNumero(token.getValor())) {
                int regIndex = indiceRegDisponible();
                if (regIndex == -1) {
                    throw new RuntimeException("No hay registros disponibles");
                }
                registroResultado = REGISTROS[regIndex];
                codigoIntermedio.add("MOV " + registroResultado + ", " + token.getValor());
                registroDisponible[regIndex] = false;
                theStack.push(registroResultado);
            } else {
                reg2 = theStack.pop();
                reg1 = theStack.pop();

                switch (token.getValor()) {
                    case "+":
                        codigoIntermedio.add("ADD " + reg1 + ", " + reg2);
                        liberarRegistro(reg2);
                        theStack.push(reg1);
                        break;
                    case "-":
                        codigoIntermedio.add("SUB " + reg1 + ", " + reg2);
                        liberarRegistro(reg2);
                        theStack.push(reg1);
                        break;
                    case "*":
                        if (!reg1.equals("AX")) {
                            codigoIntermedio.add("MOV AX, " + reg1);
                            liberarRegistro(reg1);
                        }
                        codigoIntermedio.add("MUL " + reg2);
                        codigoIntermedio.add("MOV " + reg2 + ", AX");
                        theStack.push(reg2);
                        break;
                    case "/":
                        if (!reg1.equals("AX")) {
                            codigoIntermedio.add("MOV AX, " + reg1);
                            liberarRegistro(reg1);
                        }
                        codigoIntermedio.add("DIV " + reg2);
                        codigoIntermedio.add("MOV " + reg2 + ", AX");
                        theStack.push(reg2);
                        break;
                }
            }
        }

        String registroFinal = theStack.pop();
        codigoIntermedio.add("MOV " + memoria + ", " + registroFinal);

        return codigoIntermedio;
    }

    private boolean esNumero(String valor) {
        return valor.matches("\\d+");
    }

    private int indiceRegDisponible() {
        // AX reservado
        for (int i = 1; i < registroDisponible.length; i++) {
            if (registroDisponible[i]) {
                return i;
            }
        }
        return -1;
    }

    private void liberarRegistro(String registro) {
        for (int i = 0; i < REGISTROS.length; i++) {
            if (REGISTROS[i].equals(registro)) {
                registroDisponible[i] = true;
                break;
            }
        }
    }
}