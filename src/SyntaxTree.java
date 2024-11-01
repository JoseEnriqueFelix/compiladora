public class SyntaxTree {
    private Nodo raiz;
    
    public SyntaxTree(int numTokens) {
        this.raiz = new Nodo(null, numTokens);
    }

    public Nodo getRaiz() {
        return raiz;
    }

    public void insertar(Token valor) {
        raiz.setNodoHijoNth(valor);
    }
}
