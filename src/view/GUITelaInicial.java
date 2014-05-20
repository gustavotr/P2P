package view;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUITelaInicial extends JPanel{

    private static final long serialVersionUID = 1L;
    private JLabel labelErro;
    private JButton buttonBuscar;
    private JLabel label;
    private JTextField busca;
    private int width;
    private int height;

    public GUITelaInicial(int width, int height) {        
        this.width = width;
        this.height = height;        
        
        this.inicializando();
        //this.initComponents();
    }
    
    private void inicializando(){
        
        this.setSize(width, height);
        this.setVisible(true);
        
        label = new JLabel("Aguardando a conexao dos peers");
        label.setBounds(width/2, height/2, 200, 20);
        this.add(label);
    }

    /**
     * Inicialização dos componentes da tela de início
     */
    public void initComponents() {
        
        label.setText("Peers conectados");
                
        busca = new JTextField();
        busca.setBounds(width/2, height/2, 200, 20);
        this.add(busca);
        
        buttonBuscar = new JButton("Buscar");        
        buttonBuscar.setBounds(width/2 + 210, height/2, 100, 20);
        //buttonBuscar.addActionListener(listener);
        this.add(buttonBuscar);

        labelErro = new JLabel();
        labelErro.setBounds(width/2, height*3/2, 200, 20);
        labelErro.setFont(new Font(null, Font.PLAIN, 10));
        this.add(labelErro);        
    }

    public JButton getButtonStart() {
        return buttonBuscar;
    }

    public JLabel getLabelErro() {
        return labelErro;
    }
    
    public String getBusca(){
        return busca.getText();
    }   

}
