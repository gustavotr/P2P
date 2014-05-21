package view;

import core.Processo;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUITelaInicial extends JPanel{

    private static final long serialVersionUID = 1L;
    private JLabel labelErro;
    private JButton buttonBuscar;
    private JLabel label;
    private JTextField busca;
    private int width;
    private int height;
    public static final int AGUARDANDO = 1;
    public static final int CONECTADO = 2;
    public static final int PRONTO = 3;
    private Processo processo;

    public GUITelaInicial(Processo processo, int width, int height, int status) {        
        this.processo = processo;
        this.width = width;
        this.height = height; 
        this.setSize(width, height);
        this.setVisible(true);
        
        switch (status) {
            case AGUARDANDO:  inicializando();
                     break;
            case CONECTADO:  initComponents();
                     break;
            case PRONTO: mostrarBusca();
                    break;
            default:
                    break;
        }
        
    }
    
    private void inicializando(){
    
        label = new JLabel("Aguardando a conexao dos peers");
        label.setBounds(width/2 - 100, height/2 + 50, 200, 20);
        this.add(label);
    }

    /**
     * Inicialização dos componentes da tela de início
     */
    private void initComponents() {
                                                        
        busca = new JTextField();
        busca.setBounds(width/2 - 200, height/2 - 50, 300, 20);
        this.add(busca);
        
        buttonBuscar = new JButton("Buscar");        
        buttonBuscar.setBounds(width/2 + 110, height/2 - 50, 100, 20);
        buttonBuscar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String buscar = busca.getText();
                processo.realizarBusca(buscar);
            }
        });
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

    private void mostrarBusca() {
        JLabel buscaRealizada = new JLabel(processo.getStringBuscada());
        buscaRealizada.setBounds(10, 20, getWidth() - 20, 40);
        buscaRealizada.setHorizontalAlignment(SwingConstants.CENTER);
        buscaRealizada.setFont(new Font(null, Font.BOLD, 18));
        this.add(buscaRealizada);
        
        JList<String> result = new JList<>(processo.getArquivosBuscados());
        result.setBounds(20, 100, getWidth() - 50, getHeight() - 150);
        this.add(result);
    }

}
