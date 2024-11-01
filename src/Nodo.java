public class Nodo {
    private Token token;
    private Nodo[] nodosHijos;
    private int numHijos;
    private int apuntador;

    public Nodo(Token valor, int cantNodosHijos) {
        this.token = valor;
        numHijos = cantNodosHijos;
        nodosHijos = new Nodo[numHijos];
        apuntador = 0;
    }

    public int getNumHijos(){
        return numHijos;
    }

    public Token getTokenNodoHijoNth(int i){
        return nodosHijos[i].getValor();
    }

    public void setNodoHijoNth(Token token){
        nodosHijos[apuntador++] = new Nodo(token, 0);
    }

    public Token getValor() {
        return token;
    }

}
