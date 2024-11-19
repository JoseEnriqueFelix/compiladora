import java.util.HashMap;
import java.util.List;

public class CodigoIntermedio {
    private String codigoSectionData;
    private String codigoSectionCode;
    private String codIntermedio;
    private List<List<Token>> tokensPostfix;
    private int contadorTokPostfix;
    private List<List<Token>> statements;

    public CodigoIntermedio(List<List<Token>> tokensPostfix, List<List<Token>> statements) {
        this.tokensPostfix = tokensPostfix;
        this.statements = statements;
        contadorTokPostfix = 0;
    }

    public String getSectionData() {
        return codigoSectionData;
    }

    public void setSectionData(String codigo) {
        this.codigoSectionData = codigo;
    }

    public String getCodigoSectionCode() {
        return codigoSectionCode;
    }

    public void setCodigoSectionCode(String codigo) {
        this.codigoSectionCode = codigo;
    }

    public String getCodigoIntermedio() {
        return codigoSectionData + codigoSectionCode;
    }

    public void generarCodigoIntermedio() {
        codigoSectionData = "";
        codigoSectionCode = "";
        String data = "section .data\n";
        String code = "section .code\n";

        // .data
        HashMap<String, String> variables = new HashMap<>(statements.size() * 2);
        int offsetData = 0x0;
        int segmentoData = 0x0;
        String binario = "";
        for (int i = 0; i < statements.size(); i++) {
            // boolean id; && int id;
            if (statements.get(i).get(0).getTokenNumPr() == 12) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() == 11 &&
                            statements.get(i).get(2).getTokenNum() == 6) {

                        data += statements.get(i).get(1).getValor() + " dd ?\n";
                        String segmentoHex = String.format("%04X", segmentoData);
                        String offsetHex = String.format("%04X", offsetData);
                        variables.put(statements.get(i).get(1).getValor(), offsetHex);
                        binario += segmentoHex + ":" + offsetHex + "    00000000 00000000\n";
                        offsetData += 2;
                    }
                }
            } else if (statements.get(i).get(0).getTokenNumPr() == 13) {
                if (statements.get(i).size() == 3) {
                    if (statements.get(i).get(1).getTokenNum() == 11 &&
                            statements.get(i).get(2).getTokenNum() == 6) {

                        data += statements.get(i).get(1).getValor() + " db ?\n";
                        String segmentoHex = String.format("%04X", segmentoData);
                        String offsetHex = String.format("%04X", offsetData);
                        variables.put(statements.get(i).get(1).getValor(), offsetHex);
                        binario += segmentoHex + ":" + offsetHex + "    00000000\n";
                        offsetData++;
                    }
                }
            }
        }

        setSectionData(data);
        // .code

        int offsetCode = 0x0;
        int segmentoCode = 0xA;
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i).get(0).getTokenNum() == 11) {
                // id = num; && id = valBool;
                if (statements.get(i).size() == 4) {
                    if (statements.get(i).get(1).getTokenNum() == 1 &&
                            statements.get(i).get(3).getTokenNum() == 6) {
                        if (statements.get(i).get(2).getTokenNum() == 9) {
                            contadorTokPostfix++;
                            code += "MOV " + statements.get(i).get(0).getValor() + ", "
                                    + Integer.parseInt(statements.get(i).get(2).getValor()) + "\n";
                            String segmentoHex = String.format("%04X", segmentoCode);
                            String offsetHex = String.format("%04X", offsetCode);
                            int intValue = Integer.parseInt(variables.get(statements.get(i).get(0).getValor()), 16);
                            String binaryValue = String.format("%016d",
                                    Integer.parseInt(Integer.toBinaryString(intValue)));
                            binaryValue = binaryValue.substring(0, 8) + " " + binaryValue.substring(8);
                            int intValue2 = Integer.parseInt(statements.get(i).get(2).getValor());
                            String binaryValue2 = String.format("%016d",
                                    Integer.parseInt(Integer.toBinaryString(intValue2)));
                            binaryValue2 = binaryValue2.substring(0, 8) + " " + binaryValue2.substring(8);

                            binario += segmentoHex + ":" + offsetHex + "    11000111 "
                                    + binaryValue + " " + binaryValue2 + "\n";
                            offsetCode += 5;
                            continue;
                        } else {
                            int aux;
                            if (statements.get(i).get(2).getValor().equals("false"))
                                aux = 0;
                            else
                                aux = 1;
                            code += "MOV " + statements.get(i).get(0).getValor() + ", "
                                    + aux + "\n";
                            String segmentoHex = String.format("%04X", segmentoCode);
                            String offsetHex = String.format("%04X", offsetCode);
                            int intValue = Integer.parseInt(variables.get(statements.get(i).get(0).getValor()));
                            String binaryValue = String.format("%016d",
                                    Integer.parseInt(Integer.toBinaryString(intValue)));
                            binaryValue = binaryValue.substring(0, 8) + " " + binaryValue.substring(8);
                            binario += segmentoHex + ":" + offsetHex + "    11000110 "
                                    + binaryValue + " 0000000" + aux + "\n";
                            offsetCode += 4;
                            continue;
                        }
                    }
                }
                // id = lista;
                else {
                    // TODO
                    if (statements.get(i).get(1).getTokenNum() == 1
                            && statements.get(i).get(statements.get(i).size() - 1).getTokenNum() == 6) {
                        ParsePost pp = new ParsePost(tokensPostfix.get(contadorTokPostfix++),
                                statements.get(i).get(0).getValor());
                        List<String> s = pp.doParse();
                        for (int j = 0; j < s.size(); j++)
                            code += s.get(j) + "\n";
                    }
                }
            }
        }
        setCodigoSectionCode(code);
        System.out.println(getCodigoIntermedio());
        System.out.println(binario);
    }
}
