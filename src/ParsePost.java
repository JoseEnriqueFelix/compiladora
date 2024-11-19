import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ParsePost {
    private Stack<String> theStack;
    private List<Token> input;
    private final static String[] REGISTROS = { "AX", "BX", "CX", "DX", "SP", "BP", "SI", "DI" };
    private final static String[] rrr = { "000", "001", "010", "011", "100", "101", "110", "111" };
    private boolean[] registroDisponible = { true, true, true, true, true, true, true, true };
    private List<String> codigoIntermedio;
    private String memoria;
    private List<String> binarioLista;

    public ParsePost(List<Token> s, String memoria) {
        input = s;
        codigoIntermedio = new ArrayList<>();
        binarioLista = new ArrayList<>();
        this.memoria = memoria;
        // AX reservado para multiplicación y división
        registroDisponible[0] = false;
    }

    public List<String>[] doParse(int offsetCode, int segmentoCode, HashMap<String, String> variables) {
        theStack = new Stack<>();
        Token token;
        String registroResultado;
        String reg1 = "", reg2 = "";
        String binario = "";

        for (int j = 0; j < input.size(); j++) {
            String segmentoHex = "", offsetHex = "";
            token = input.get(j);
            if (esNumero(token.getValor())) {
                int regIndex = indiceRegDisponible();
                if (regIndex == -1) {
                    throw new RuntimeException("No hay registros disponibles");
                }
                registroResultado = REGISTROS[regIndex];
                codigoIntermedio.add("MOV " + registroResultado + ", " + token.getValor());
                int intValue = Integer.parseInt(token.getValor());
                String binaryValue = String.format("%016d",
                        Integer.parseInt(Integer.toBinaryString(intValue)));
                binaryValue = binaryValue.substring(0, 8) + " " + binaryValue.substring(8);
                segmentoHex = String.format("%04X", segmentoCode);
                offsetHex = String.format("%04X", offsetCode);
                binario += segmentoHex + ":" + offsetHex + "    10111" + rrr[regIndex] + " " + binaryValue + "\n";
                offsetCode += 3;
                binarioLista.add(binario);
                binario = "";
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
                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    00000011 11" + rrr[obtenerIndice(reg1)]
                                + rrr[obtenerIndice(reg2)]
                                + "\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";

                        break;
                    case "-":
                        codigoIntermedio.add("SUB " + reg1 + ", " + reg2);
                        liberarRegistro(reg2);
                        theStack.push(reg1);

                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    00010111 11" + rrr[obtenerIndice(reg1)]
                                + rrr[obtenerIndice(reg2)]
                                + "\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";
                        break;
                    case "*": // 11110111 00100
                        if (!reg1.equals("AX")) {
                            codigoIntermedio.add("MOV AX, " + reg1);
                            liberarRegistro(reg1);
                            segmentoHex = String.format("%04X", segmentoCode);
                            offsetHex = String.format("%04X", offsetCode);
                            binario += segmentoHex + ":" + offsetHex + "    10001011 11000" + rrr[obtenerIndice(reg1)]
                                    + "\n";
                            offsetCode += 2;
                            binarioLista.add(binario);
                            binario = "";
                        }
                        codigoIntermedio.add("MUL " + reg2);
                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    11110111 00100" + rrr[obtenerIndice(reg2)]
                                + "\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";
                        codigoIntermedio.add("MOV " + reg2 + ", AX");
                        theStack.push(reg2);
                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    10001011 11"
                                + rrr[obtenerIndice(reg2)] + "000\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";
                        break;
                    case "/":
                        if (!reg1.equals("AX")) {
                            codigoIntermedio.add("MOV AX, " + reg1);
                            liberarRegistro(reg1);
                            segmentoHex = String.format("%04X", segmentoCode);
                            offsetHex = String.format("%04X", offsetCode);
                            binario += segmentoHex + ":" + offsetHex + "    10001011 11000" + rrr[obtenerIndice(reg1)]
                                    + "\n";
                            offsetCode += 2;
                            binarioLista.add(binario);
                            binario = "";
                        }
                        codigoIntermedio.add("DIV " + reg2);
                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    11110111 00110" + rrr[obtenerIndice(reg2)]
                                + "\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";
                        codigoIntermedio.add("MOV " + reg2 + ", AX");
                        theStack.push(reg2);
                        segmentoHex = String.format("%04X", segmentoCode);
                        offsetHex = String.format("%04X", offsetCode);
                        binario += segmentoHex + ":" + offsetHex + "    10001011 11"
                                + rrr[obtenerIndice(reg2)] + "000\n";
                        offsetCode += 2;
                        binarioLista.add(binario);
                        binario = "";
                        break;
                }
            }
        }

        String registroFinal = theStack.pop();
        codigoIntermedio.add("MOV " + memoria + ", " + registroFinal);

        int intValue = Integer.parseInt(variables.get(memoria));
        String binaryValue = String.format("%016d",
                Integer.parseInt(Integer.toBinaryString(intValue)));
        binaryValue = binaryValue.substring(0, 8) + " " + binaryValue.substring(8);
        String segmentoHex = String.format("%04X", segmentoCode);
        String offsetHex = String.format("%04X", offsetCode);

        binario += segmentoHex + ":" + offsetHex + "    10001011 00" + rrr[obtenerIndice(registroFinal)]
                + "110 " + binaryValue + "\n";

        offsetCode += 4;
        binarioLista.add(binario);
        binario = "";

        return new List[] { codigoIntermedio, binarioLista };
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

    private int obtenerIndice(String s) {
        for (int i = 0; i < REGISTROS.length; i++) {
            if (REGISTROS[i].equals(s))
                return i;
        }

        return -1;
    }
}