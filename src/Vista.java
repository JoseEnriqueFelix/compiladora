
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Font;
//import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Vista extends JFrame implements ComponentListener, ActionListener {
    private JMenuBar mb;
    private JMenu x;
    private JMenuItem m1, m2, m3, m4;
    private JTextArea ta1, ta2, codInt, binario;
    private JLabel error;
    private JScrollPane sp1, sp2, spCodInt, spBinario;
    private JButton btnLimpiarTa1, btnLimpiarTa2, btnLimpiarParser, btnTokens, btnParser;
    private JTextField txtParser, txtError;
    private File archivoAbierto;
    private ArrayList<Token> tokens;
    private InToPost traductor;
    Lexer lexer;
    Parser parser;

    public static void main(String[] args) {
        new Vista();
    }

    public Vista() {
        super("Lenguaje L#");
        Interfaz();
        Escuchadores();
        lexer = new Lexer();
        parser = new Parser();
        tokens = new ArrayList<Token>();
        traductor = null;
    }

    public void Interfaz() {
        // cosas genericas de la interfaz
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setSize(1600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 25));
        UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 16));
        // instanciar los componentes de la interfaz
        mb = new JMenuBar();
        x = new JMenu("Archivo");

        m1 = new JMenuItem("Abrir Ejemplo");
        m2 = new JMenuItem("Guardar");
        m3 = new JMenuItem("Guardar como");
        m4 = new JMenuItem("Gramatica");

        ta1 = new JTextArea();
        ta2 = new JTextArea();
        codInt = new JTextArea();
        binario = new JTextArea();

        error = new JLabel();

        sp1 = new JScrollPane(ta1);
        sp2 = new JScrollPane(ta2);
        spCodInt = new JScrollPane(codInt);
        spBinario = new JScrollPane(binario);

        TextLineNumber lineas = new TextLineNumber(ta1);
        TextLineNumber lineas2 = new TextLineNumber(ta2);
        sp1.setRowHeaderView(lineas);
        sp2.setRowHeaderView(lineas2);

        btnLimpiarTa1 = new JButton("limpiar");
        btnLimpiarTa2 = new JButton("limpiar");
        btnLimpiarParser = new JButton("limpiar");
        btnTokens = new JButton("Tokens");
        btnParser = new JButton("Parser");
        txtParser = new JTextField();
        txtError = new JTextField();
        txtError.setForeground(Color.RED);
        txtError.setFont(new Font("SansSerif", Font.BOLD, 18));

        x.add(m1);
        x.add(m2);
        x.add(m3);
        x.add(m4);

        mb.add(x);
        setJMenuBar(mb);
        setVisible(true);
    }

    public void Escuchadores() {
        addComponentListener(this);
        m1.addActionListener(this);
        m2.addActionListener(this);
        m3.addActionListener(this);
        m4.addActionListener(this);
        btnLimpiarTa1.addActionListener(this);
        btnLimpiarTa2.addActionListener(this);
        btnLimpiarParser.addActionListener(this);
        btnTokens.addActionListener(this);
        btnParser.addActionListener(this);
    }

    public void abrirArchivo() {
        String nombreArchivo = "ejemplo.txt";

        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                br.close();
                ta1.setText(content.toString());
                archivoAbierto = archivo; // archivo abierto actualmente es gramatica.txt
            } else {
                JOptionPane.showMessageDialog(this, "El archivo no existe.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + e.getMessage());
        }
    }

    public void guardarArchivo() {
        if (archivoAbierto != null) {
            try {
                String textoAGaurdar = ta1.getText();
                FileWriter fileWriter = new FileWriter(archivoAbierto);
                fileWriter.write(textoAGaurdar);
                fileWriter.close();
                JOptionPane.showMessageDialog(null, "El archivo se ha guardado exitosamente.");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ha ocurrido un error al guardar el archivo.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay un archivo abierto para guardar.");
        }
    }

    public void guardarArchivoComo() {
        JFileChooser fileChooser = new JFileChooser();
        // filtro de extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // extraer el texto del JTextArea
                String textToSave = ta1.getText();
                // Escribir el texto al archivo
                FileWriter fileWriter = new FileWriter(selectedFile);
                fileWriter.write(textToSave);
                fileWriter.close();
                JOptionPane.showMessageDialog(null, "El archivo se ha guardado exitosamente.");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ha ocurrido un error al guardar el archivo.");
            }
        }
    }

    public void abrirGramatica() {
        String nombreArchivo = "gramatica.txt";

        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                br.close();
                ta1.setText(content.toString());
                archivoAbierto = archivo; // archivo abierto actualmente es gramatica.txt
            } else {
                JOptionPane.showMessageDialog(this, "El archivo no existe.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + e.getMessage());
        }
    }

    // darles una posicion y tamaño relativos a los componentes
    @Override
    public void componentResized(ComponentEvent e) {
        int w = this.getWidth();
        int h = this.getHeight();
        // posicion del textArea
        int textAreaX = (int) (w * 0.03);
        int textAreaY = (int) (h * 0.11);
        // tamaño del textArea para programa
        int textAreaWidth = (int) (w * .40);
        int textAreaHeight = (int) (h * .40);
        // darle las medidas al scrollpane/textarea
        sp1.setBounds(textAreaX, textAreaY, textAreaWidth, textAreaHeight);

        // posicion del textArea para tokens
        int textArea2X = (int) (w * 0.57);
        int textArea2Y = (int) (h * 0.11);
        // tamaño del textArea
        int textAreaWidth2 = (int) (w * 0.40);
        int textAreaHeight2 = (int) (h * 0.45);
        // darle las medidas al scrollpane/textarea
        sp2.setBounds(textArea2X, textArea2Y, textAreaWidth2, textAreaHeight2);

        // posicion del boton limpiarTa1
        int btnLimpiarX = (int) (w * 0.03);
        int btnLimpiarY = textAreaY + textAreaHeight + 10;
        // tamaño del boton limpiar
        int btnLimpiarWidth = (int) (w * .08);
        int btnLimpiarHeight = (int) (h * .06);
        // posicion spcodint
        textAreaX = (int) (w * 0.03);
        textAreaY = (int) (h * 0.6);
        // tamaño del textArea para programa
        textAreaWidth = (int) (w * .40);
        textAreaHeight = (int) (h * .30);
        // darle las medidas al scrollpane/textarea
        spCodInt.setBounds(textAreaX, textAreaY, textAreaWidth, textAreaHeight);

        // posicion spbinario
        textAreaX = (int) (w * 0.55);
        textAreaY = (int) (h * 0.6);
        // tamaño del textArea para programa
        textAreaWidth = (int) (w * .40);
        textAreaHeight = (int) (h * .30);
        // darle las medidas al scrollpane/textarea
        spBinario.setBounds(textAreaX, textAreaY, textAreaWidth, textAreaHeight);

        // darle las medidas al btnLimpiar
        btnLimpiarTa1.setBounds(btnLimpiarX, btnLimpiarY, btnLimpiarWidth, btnLimpiarHeight);

        // posicion del boton limpiarTa1
        int btnLimpiarTa2X = (int) (w * 0.57);
        int btnLimpiarTa2Y = textAreaY + textAreaHeight + 10;
        // tamaño del boton limpiar
        int btnLimpiarTa2Width = (int) (w * .08);
        int btnLimpiarTa2Height = (int) (h * .06);
        // darle las medidas al btnLimpiar
        btnLimpiarTa2.setBounds(btnLimpiarTa2X, btnLimpiarTa2Y, btnLimpiarTa2Width, btnLimpiarTa2Height);
        // posicion del boton limpiarTa1
        int errorX = (int) (w * 0.7);
        int errorY = textAreaY + textAreaHeight + 10;
        // tamaño del boton limpiar
        int errorWidth = (int) (w * .2);
        int errorHeight = (int) (h * .06);
        // darle las medidas al btnLimpiar
        error.setBounds(errorX, errorY, errorWidth, errorHeight);

        // posicion del boton tokens
        int btnTokensX = (int) (w * 0.76);
        int btnTokensY = (int) (h * 0.03);
        // tamaño del boton tokens
        int btnTokensWidth = (int) (w * .08);
        int btnTokensHeight = (int) (h * .05);
        // darle las medidas al btn tokens
        btnTokens.setBounds(btnTokensX, btnTokensY, btnTokensWidth, btnTokensHeight);

        // posicion del boton parser
        int btnParserX = (int) (w * 0.46);
        int btnParserY = (int) (h * 0.14);
        // tamaño del boton tokens
        int btnParserWidth = (int) (w * .08);
        int btnParserHeight = (int) (h * .06);
        // darle las medidas al btn tokens
        btnParser.setBounds(btnParserX, btnParserY, btnParserWidth, btnParserHeight);
        // posicion del textField parser
        int txtParserX = (int) (w * 0.46);
        int txtParserY = (int) (h * 0.22);
        // tamaño del textfield parser
        int txtParserWidth = (int) (w * .08);
        int txtParserHeight = (int) (h * .06);
        // darle las medidas al textfield parser
        txtParser.setBounds(txtParserX, txtParserY, txtParserWidth, txtParserHeight);
        // darle posicion al btnlimpiar parser
        int y = (int) (h * 0.3);
        btnLimpiarParser.setBounds(txtParserX, y, txtParserWidth, txtParserHeight);
        // posicion del textField error
        int txtErrorX = (int) (w * 0.05);
        int txtErrorY = (int) (h * 0.02);
        // tamaño del textfield parser
        int txtErrorWidth = (int) (w * .25);
        int txtErrorHeight = (int) (h * 0.07);
        // darle las medidas al textfield parser
        txtError.setBounds(txtErrorX, txtErrorY, txtErrorWidth, txtErrorHeight);

        add(sp1);
        add(sp2);
        add(spCodInt);
        add(spBinario);
        add(btnLimpiarTa1);
        add(btnLimpiarTa2);
        add(btnLimpiarParser);
        add(btnTokens);
        add(btnParser);
        add(txtParser);
        add(txtError);
        add(error);
        validate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == m1) {
            abrirArchivo();
            return;
        }
        if (e.getSource() == m2) {
            guardarArchivo();
            return;
        }
        if (e.getSource() == m3) {
            guardarArchivoComo();
            return;
        }
        if (e.getSource() == m4) {
            abrirGramatica();
            return;
        }
        if (e.getSource() == btnLimpiarTa1) {
            ta1.setText("");
            return;
        }
        if (e.getSource() == btnLimpiarTa2) {
            ta2.setText("");
            return;
        }
        if (e.getSource() == btnLimpiarParser) {
            txtParser.setText("");
            return;
        }
        if (e.getSource() == btnTokens) {
            if (!tokens.isEmpty())
                tokens.clear();
            tokens = lexer.analizadorLexico(ta1.getText());
            ImprimirTokens();
            return;
        }
        if (e.getSource() == btnParser) {
            if (tokens.isEmpty()) {
                System.out.println("Actualmente no hay tokens");
                return;
            }
            txtParser.setText("");
            boolean b = parser.parser(tokens);
            txtParser.setText("" + b);

            if (b) {
                AnalizadorSemantico as = new AnalizadorSemantico();
                b = as.analisisSemantico(parser.getSyntaxTrees());
                System.out.println("" + b);
                if (!b) {
                    txtError.setText(as.getError());
                    codInt.setText("");
                    binario.setText("");
                    System.out.println("Ahhh valio churro");
                    Rutinas.Mensaje("Ayudaaaaaa");
                } else {
                    txtError.setText("");
                    List<List<Token>> aux = parser.getOperacionesListas();
                    List<List<Token>> statements = parser.getStatements();
                    imprimirTokensListas(aux);
                    parser.vaciaOperacionesListas();
                    traductor = new InToPost(aux);
                    List<List<Token>> aux2 = traductor.doTrans();
                    CodigoIntermedio codInter = new CodigoIntermedio(aux2, statements);

                    codInter.generarCodigoIntermedio();
                    codInt.setText(codInter.getCodigoIntermedio());
                    binario.setText(codInter.getBinario());
                }
            }
            return;
        }
    }

    private void imprimirTokensListas(List<List<Token>> param) {
        for (int i = 0; i < param.size(); i++) {
            System.out.println();
            for (int j = 0; j < param.get(i).size(); j++) {
                System.out.print(param.get(i).get(j).getValor());
            }
            System.out.println();
        }
    }

    private void ImprimirTokens() {
        String tokes = "";
        // Limpiando TextArea
        ta2.setText("");
        // Agregando a TextArea
        for (int i = 0; i < tokens.size(); i++)
            tokes += tokens.get(i).getTipoDeToken() + " => " + tokens.get(i).getValor() + "\n";
        ta2.setText(tokes);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
