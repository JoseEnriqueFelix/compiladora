public class StackX {
    private int maxSize;
    private Token[] stackArray;
    private int top;

    public StackX(int s) {
        maxSize = s;
        stackArray = new Token[maxSize];
        top = -1;
    }

    public void push(Token j) {
        stackArray[++top] = j;
    }

    public Token pop() {
        return stackArray[top--];
    }

    public Token peek() {
        return stackArray[top];
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public int size() {
        return top + 1;
    }

    public String peekN(int n) {
        return stackArray[n].getValor();
    }

    public void displayStack(String s) {
        System.out.print(s);
        System.out.print("stack (bottom ==> top): ");
        for (int j = 0; j < size(); j++) {
            System.out.print(peekN(j));
            System.out.print(' ');
        }
        System.out.println("");
    }
}