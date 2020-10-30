package servidor;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class MainController implements Initializable {
    @FXML
    private TableView<Cliente> tb_Conexoes;
    @FXML
    private TableColumn<?, ?> tc_nome;
    @FXML
    private TableColumn<?, ?> tc_IP;
    @FXML
    private TableColumn<?, ?> tc_Porta;
    @FXML
    private TableColumn<?, ?> tc_Status;
    @FXML
    private Label lb_titulo;
    @FXML
    private JFXButton btn_conectar;
    
    private ServerSocket server;
    private Socket socket;
    private Thread thread;
    
    private ArrayList<Cliente> lista = new ArrayList();
    private Boolean estado = false;
    private int porta;//1 porta disponivel
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tc_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tc_IP.setCellValueFactory(new PropertyValueFactory<>("ip"));
        tc_Porta.setCellValueFactory(new PropertyValueFactory<>("porta"));
        
        EstadoInicial();
    }    
    
    private void EstadoInicial(){
        lb_titulo.setText("Servidor Offline");
        
        btn_conectar.setText("Conectar");
        btn_conectar.textFillProperty().set(Color.LIME);
        btn_conectar.setStyle("-fx-border-color: #00ff00");
        
        porta = 8081;//porta que sera do servidor
        server = null;
        socket = null;
    }
    
    private void EstadoOnline(){
        lb_titulo.setText("Servidor Online: "+getIp_Processado());
        
        btn_conectar.setText("Desconectar");
        btn_conectar.textFillProperty().set(Color.RED);
        btn_conectar.setStyle("-fx-border-color: #ff0000");
    }

    @FXML
    private void btn_conectar(MouseEvent event) {
        if(!estado){
            try{
                server = new ServerSocket(new Integer(porta));
                porta++;
                EstadoOnline();
                
                ctrServidor ctr = new ctrServidor(tb_Conexoes, server);
                ctr.setPorta(porta);
                estado = !estado;

                //ctr.run();
                thread = new Thread(ctr);
                thread.start();             
            }catch(IOException e){
                EstadoOnline();
                porta--;
                estado = !estado;
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Ocorreu um erro ao conectar o servidor: "+ e.getMessage());

                alert.showAndWait();
            }
        }else{
            try{  
                server.close();
                server = null;
                EstadoInicial();
                //thread.stop();
                
                lista.clear();
                tb_Conexoes.getItems().clear(); 
                
                estado = !estado;
            }catch(IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Ocorreu um erro ao desconectar o servidor: " + e.getClass());

                alert.showAndWait();
            }
        }
    }    
   
    /*gets*/
        public TableView<Cliente> getTb_Conexoes() {
            return tb_Conexoes;
        }

        public Boolean getEstado() {
            return estado;
        }

        public int getPorta() {
            return porta;
        }
        
        public String getIp_Processado(){
            String systemipaddress = ""; 
            try{ 
                URL url_name = new URL("http://bot.whatismyipaddress.com"); 

                BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream())); 

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
