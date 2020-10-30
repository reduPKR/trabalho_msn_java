package cliente;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class MainController implements Initializable {       
    @FXML
    private Label lb_titulo;
    @FXML
    private JFXButton btn_conectar;
    @FXML
    private TableView<ctrCliente> tb_Lista;
    @FXML
    private TableColumn<?, ?> tc_nome;
    @FXML
    private JFXTextArea ta_historico;
    @FXML
    private JFXTextField tf_mensagem;
    @FXML
    private JFXButton btn_enviar;
    @FXML
    private JFXTextField tf_Servidor;
    @FXML
    private JFXTextField tf_nick;
    
    private Boolean estado;
    private ServerSocket server;
    private String mensagem;
    private ctrCliente cliente;
    private Thread thread;
    /*private ObjectOutputStream output;
    private ObjectInputStream input;*/
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        estado = false;
        
        tc_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        EstadoInicial();
    }        

    private void EstadoInicial(){
        lb_titulo.setText("Cliente Offline");
        
        btn_conectar.setText("Conectar");
        btn_conectar.textFillProperty().set(Color.LIME);
        btn_conectar.setStyle("-fx-border-color: #00ff00");
        
        tf_Servidor.setDisable(false);
        tf_nick.setDisable(false);
        
        btn_enviar.setDisable(true);
        tf_mensagem.setDisable(true);
        tf_mensagem.clear();
        
        tb_Lista.getItems().clear();
        server = null;
    }
    
    private void EstadoOnline(){
        lb_titulo.setText("Cliente Online");
        
        btn_conectar.setText("Desconectar");
        btn_conectar.textFillProperty().set(Color.RED);
        btn_conectar.setStyle("-fx-border-color: #ff0000");
        
        tf_Servidor.setDisable(true);
        tf_nick.setDisable(true);
        
        btn_enviar.setDisable(false);
        tf_mensagem.setDisable(false);
    }
    
    @FXML
    private void btn_conectar(MouseEvent event) {
        if(!estado){ 
            //(String Nome, String IP, int Porta
            cliente = new ctrCliente(tf_nick.getText(),getIp_Processado(), 8081);
            cliente.setIP_Servidor( tf_Servidor.getText());
            
            cliente.setLista(tb_Lista);
            cliente.setTa_historico(ta_historico);
            
            if(cliente.Connect()){ 
                try {
                    server = new ServerSocket(new Integer(cliente.getPorta()));
                    
                    EstadoOnline();
                    estado = !estado;
                    
                    cliente.setServer(server);
                    thread = new Thread(cliente);
                    thread.start();   
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    EstadoOnline();
                    estado = !estado;
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao se conectar com servidor: ");

                alert.showAndWait();
            }
            
        }else{
            if(cliente.Disconnect()){ 
                try{  
                    server.close();
                    server = null;

                    EstadoInicial();
                    estado = !estado;
                }catch(IOException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Ocorreu um erro ao desconectar o servidor: " + e.getClass());

                    alert.showAndWait();
                }  
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao se desconectar do servidor: ");

                alert.showAndWait();
            }
        }
    }

    @FXML
    private void btn_enviar(MouseEvent event) { 
        if(tb_Lista.getSelectionModel().getSelectedItem() != null){
            cliente.Send(tf_mensagem.getText(),tb_Lista.getSelectionModel().getSelectedItem());
            tf_mensagem.clear();
            ta_historico.clear();
            ta_historico.setText(cliente.getHistoricoFormatado(tb_Lista.getSelectionModel().getSelectedItem().getIP()));
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro nenhum contato selecionado: ");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void tv_click(MouseEvent event) {
        ta_historico.clear();
        ta_historico.setText(cliente.getHistoricoFormatado(tb_Lista.getSelectionModel().getSelectedItem().getIP()));
    }
    
    /*gets*/
    public String getIp_Processado(){
        String systemipaddress = ""; 
        try{ 
            URL url_name = new URL("http://bot.whatismyipaddress.com"); 

            BufferedReader sc = 
            new BufferedReader(new InputStreamReader(url_name.openStream())); 

            // reads system IPAddress 
            systemipaddress = sc.readLine().trim(); 
        } 
        catch (Exception e){ 
            systemipaddress = "127.0.0.1"; 
        } 
        return systemipaddress;
    }
    /*gets*/
}
