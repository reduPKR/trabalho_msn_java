package cliente;

import com.jfoenix.controls.JFXTextArea;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class ctrCliente implements Runnable{
    private String nome;
    private String ip;
    private String ip_servidor;
    private int porta;
    private TableView<ctrCliente> lista;
    private ArrayList<Mensagem> historico;
    private ServerSocket server;
    private JFXTextArea ta_historico;

    public ctrCliente(String nome, String ip, String ip_servidor, int porta) {
        this.nome = nome;
        this.ip = ip;
        this.ip_servidor = ip_servidor;
        this.porta = porta;
        server = null;
        
        historico = new ArrayList();
    }

    public ctrCliente(String nome, String ip, int porta) {
        this.nome = nome;
        this.ip = ip;
        this.porta = porta;
        server = null;
        
        historico = new ArrayList();
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getIP_Servidor() {
        return ip_servidor;
    }

    public void setIP_Servidor(String ip_servidor) {
        this.ip_servidor = ip_servidor;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public TableView<ctrCliente> getLista() {
        return lista;
    }

    public void setLista(TableView<ctrCliente> lista) {
        this.lista = lista;
    }

    public void setTa_historico(JFXTextArea ta_historico) {
        this.ta_historico = ta_historico;
    }
    
    /*Historico*/    
    public ArrayList<Mensagem> getHistorico(String destino){
        ArrayList<Mensagem> hist = new ArrayList();
        
        for (int i = 0; i < historico.size(); i++) {
            if(historico.get(i).getDestino().getIP().equals(ip) && historico.get(i).getOrigem().getIP().equals(destino)){
                hist.add(historico.get(i));
            }
        }
        
        return hist;
    }

    public String getHistoricoFormatado(String destino){
        String hist = null;
        
        for (int i = 0; i < historico.size(); i++) {
            if(historico.get(i).getDestino().getIP().equals(ip) && historico.get(i).getOrigem().getIP().equals(destino)
                    || historico.get(i).getOrigem().getIP().equals(ip) && historico.get(i).getDestino().getIP().equals(destino)){
                if(hist == null){
                    hist = historico.get(i).getMsg()+"\n";
                }else{
                    hist += historico.get(i).getMsg()+"\n";
                }
            }
        }
        
        return hist;
    }   
    
    
    /*Historico*/
    //Enviar a mensagem ara o servidor
    public boolean Connect() {
        try{
            //1 = connect => 1,IP,Nome
            String mensagem = "1,"+ip + "," + nome;
            
            Socket cliente = new Socket(ip_servidor, 8081);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.flush();
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
            /*Servidor responde com a lista de clientes ativos*/
            String listac = (String) input.readObject();
            String[] clientes = listac.split(",");
            
            //ip, nome, porta. Fiz separado por isso esta invertido
            for (int i = 1; i <= clientes.length - 3; i += 3){
                lista.getItems().add(new ctrCliente(clientes[i+1], clientes[i], Integer.parseInt(clientes[i + 2])));
            }
            porta = Integer.parseInt(clientes[clientes.length - 1]);
            
            //output.writeObject("FIM");
            //output.flush();
            input.close();
            output.close();

            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public boolean Disconnect(){
        try{
            //1 = connect => 1,IP,Nome
            String mensagem = "3,"+ip;
            
            Socket cliente = new Socket(ip_servidor, 8081);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.flush();
            output.writeObject(mensagem);
            
            output.close();
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    //Threads
    @Override
    public void run(){
        if (server != null && server.isBound()){
            try{
                Object pacote;
                while (true){
                    Socket cliente = server.accept();
                    ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                    pacote = input.readObject();
                    String resposta;
                    String[] resp;
                    
                    if (pacote instanceof String){
                        resposta = (String) (pacote);
                        resp = resposta.split(",");
                        
                        //resposta = tipo, ip, nome, porta e a classe é nome ip e porta 
                        if(resp[0].equals("3")){//Alguem desconectou
                            lista.getItems().clear();
                            //ip, nome, porta. Fiz separado por isso esta invertido
                            for (int i = 1; i <= resp.length - 3; i += 3){
                                lista.getItems().add(new ctrCliente(resp[i+1], resp[i], Integer.parseInt(resp[i + 2])));
                            }
                        }else
                        if(resp[0].equals("4")){//Nova conexao
                            lista.getItems().add(new ctrCliente(resp[2], resp[1], Integer.parseInt(resp[3])));
                        }else
                        if(resp[0].equals("5")){
                            /*Recebe a mensagem que vem como send*/
                            ctrCliente origem = null;
                            
                            for (int i = 0; i < lista.getItems().size() && origem == null; i++) {
                                if(lista.getItems().get(i).getIP().equals(resp[1])){
                                    origem = lista.getItems().get(i);
                                }
                            }
                            
                            historico.add(new Mensagem(origem, this, resp[2]+": "+resp[3]));
                            ta_historico.setText(getHistoricoFormatado(origem.getIP()));
                        }
                    } 
                    cliente.close();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void Send(String msg, ctrCliente destino){
        historico.add(new Mensagem(this, destino, nome+": "+msg));

        try{
            String enviar = "5,"+ip+","+nome+","+msg;
            Socket cliente = new Socket(destino.getIP(), destino.getPorta());
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.flush();
            output.writeObject(enviar);
            output.close();
            cliente.close();
        } catch (IOException ex){
            System.out.println(ex.getCause());
        }
    }
}
