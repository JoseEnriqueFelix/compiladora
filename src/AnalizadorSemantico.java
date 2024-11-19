public class AnalizadorSemantico {
    private String error;

    public String getError() {
        return error;
    }

    public boolean analisisSemantico(SyntaxTree[] sts) {
        for (int i = 0; i < sts.length; i++) {
            Nodo aux = sts[i].getRaiz();
            if (aux.getNumHijos() == 4 && aux.getTokenNodoHijoNth(0).getTokenNum() == 11
                    && aux.getTokenNodoHijoNth(1).getTokenNum() == 1
                    && aux.getTokenNodoHijoNth(2).getTokenNum() == 10
                    && aux.getTokenNodoHijoNth(2).getTokenNumPr() == 14) {
                if (!evaluarExistenciaIdBoolean(sts, i, aux.getTokenNodoHijoNth(0).getValor()))
                    return false;
            }
            if (aux.getNumHijos() >= 4) {
                if (aux.getTokenNodoHijoNth(0).getTokenNum() == 11 && aux.getTokenNodoHijoNth(1).getTokenNum() == 1
                        && aux.getTokenNodoHijoNth(2).getTokenNum() != 10) {
                    if (!evaluarExistenciaIdInt(sts, i, aux.getTokenNodoHijoNth(0).getValor()))
                        return false;
                    if (!evaluarLista(sts, i, aux, 2))
                        return false;
                }
            }
        }
        return true;
    }

    public boolean evaluarLista(SyntaxTree[] sts, int i, Nodo aux, int empiezaLista) {
        for (int j = empiezaLista; j < aux.getNumHijos(); j++) {
            if (aux.getTokenNodoHijoNth(j).getTokenNum() == 11)
                if (!evaluarExistenciaIdInt(sts, i, aux.getTokenNodoHijoNth(j).getValor()))
                    return false;
        }
        for (int j = empiezaLista + 1; j < aux.getNumHijos(); j++) {
            if (aux.getTokenNodoHijoNth(j).getTokenNum() == 9
                    && aux.getTokenNodoHijoNth(j).getValor().equals("0")
                    && aux.getTokenNodoHijoNth(j - 1).getTokenNum() == 4) {
                error = "Division entre 0 en instr" + i;
                return false;
            }
        }
        return true;
    }

    public boolean evaluarExistenciaIdBoolean(SyntaxTree[] sts, int limSup, String valorId) {
        if (limSup > 0) {
            for (int i = limSup - 1; i >= 0; i--) {
                Nodo aux = sts[i].getRaiz();
                if (aux.getTokenNodoHijoNth(0).getTokenNum() == 10
                        && aux.getTokenNodoHijoNth(0).getTokenNumPr() == 13
                        && aux.getTokenNodoHijoNth(1).getTokenNum() == 11
                        && aux.getTokenNodoHijoNth(1).getValor().equals(valorId)) {
                    return true;
                } else if (aux.getTokenNodoHijoNth(0).getTokenNum() == 10
                        && aux.getTokenNodoHijoNth(0).getTokenNumPr() == 12
                        && aux.getTokenNodoHijoNth(1).getTokenNum() == 11
                        && aux.getTokenNodoHijoNth(1).getValor().equals(valorId)) {
                    error = "ID definido como int en vez de boolean en instr" + limSup + " => instr" + i;
                    return false;
                }
            }
        }
        error = "ID no definido en instr" + limSup;
        return false;
    }

    public boolean evaluarExistenciaIdInt(SyntaxTree[] sts, int limSup, String valorId) {
        if (limSup > 0) {
            for (int i = limSup - 1; i >= 0; i--) {
                Nodo aux = sts[i].getRaiz();
                if (aux.getTokenNodoHijoNth(0).getTokenNum() == 10
                        && aux.getTokenNodoHijoNth(0).getTokenNumPr() == 12
                        && aux.getTokenNodoHijoNth(1).getTokenNum() == 11
                        && aux.getTokenNodoHijoNth(1).getValor().equals(valorId)) {
                    return true;
                } else if (aux.getTokenNodoHijoNth(0).getTokenNum() == 10
                        && aux.getTokenNodoHijoNth(0).getTokenNumPr() == 13
                        && aux.getTokenNodoHijoNth(1).getTokenNum() == 11
                        && aux.getTokenNodoHijoNth(1).getValor().equals(valorId)) {
                    error = "ID definido como boolean en vez de int en instr" + limSup + " => instr" + i;
                    return false;
                }
            }
        }
        error = "ID no definido en instr" + limSup;
        return false;
    }
}
