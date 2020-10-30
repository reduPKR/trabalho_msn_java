package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableView;

public class ctrServidor implements Runnable{
    private TableView<Cliente> tb_Conexoes;
    private ServerSocket server;
    private int porta;
    
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ctrServidor(TableView<Cliente> tb_Conexoes, ServerSocket server) {
        this.tb_Conexoes = tb_Conexoes;
        this.server = server;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
    
    @Override
    public void run(){
        if (server != null && server.isBound()){
            try{
                Object pacote;
                while (true){
                    Socket cliente = server.accept();
                    System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                    input = new ObjectInputStream(cliente.getInputStream());
                    
                    pacote = input.readObject();
                    String resposta;
                    String[] resp;
                    
                    if (pacote instanceof String){
                        resposta = (String) (pacote);
                        resp = resposta.split(",");
                        
                        if(resp[0].equals("1")){
                            /*Add novo cliente a LISTA ip, nome, porta*/
                            tb_Conexoes.getItems().add(new Cliente(resp[1], resp[2], porta));
                            porta++;
                            Accept(cliente);//Envia a resposta
                            PropagarConexao("4,"+resp[1]+","+resp[2]+","+(porta-1));
                        }else
                        if(resp[0].equals("3")){//Disconectar
                            int pos = -1;
                            for(int i = 0;i < tb_Conexoes.getItems().size();i++){
                                if(tb_Conexoes.getItems().get(i).getIp().equals(resp[1]))
                                    pos = i;
                            }
                            
                            if(pos != -1){
                                System.out.println("Ip "+tb_Conexoes.getItems().get(pos).getIp()+" se desconectou");
                                tb_Conexoes.getItems().remove(pos);   
                            }                         
                            PropagarDisconnet();
                        }
                    }

                    cliente.close();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    
    public void PropagarConexao(String novo){
        for (int i = 0; i < tb_Conexoes.getItems().size() - 1 ; i++){
            Cliente cli = tb_Conexoes.getItems().get(i);
            
            Integer p = cli.getPorta();
            try {
                Socket skt = new Socket(cli.getIp(), p);//Unoeste
                //Socket skt = new Socket("127.0.0.1", 8082);
                output = new ObjectOutputStream(skt.getOutputStream());
                output.flush();
                output.writeObject(novo);
                output.close();
            } catch (IOException e) {
                System.out.println("Erro "+e.getMessage());
            }
        }
    }
    
    public void Accept(Socket cliente){
        try{
            output = new ObjectOutputStream(cliente.getOutputStream());
            output.flush();
            StringBuilder acceptRes = new StringBuilder("2");
            Cliente c = tb_Conexoes.getItems().get(0);
            for (int i = 0; i < tb_Conexoes.getItems().size(); i++){
                c = tb_Conexoes.getItems().get(i);
                acceptRes.append(",").
                        append(c.getIp()).
                        append(",").
                        append(c.getNome()).
                        append(",").
                        append(c.getPorta());
            }
            
            output.writeObject(acceptRes.toString());
            output.close();
            
            //Propaga a nova conexao para todos
            //PropagarConexao("4,"+c.getIp()+","+c.getNome()+","+c.getPorta());
        } catch (Exception e){
            System.out.println(" "+e.getMessage());
        }
    }
    
    public void PropagarDisconnet(){
        /*Remonta a lista*/
        StringBuilder acceptRes = new StringBuilder("3");
        Cliente c = tb_Conexoes.getItems().get(0);
        for (int i = 0; i < tb_Conexoes.getItems().size(); i++){
            c = tb_Conexoes.getItems().get(i);
            acceptRes.append(",").
                    append(c.getIp()).
                    append(",").
                    append(c.getNome()).
                    append(",").
                    append(c.getPorta());
        }
        
        /*Envia a lista*/
        for (int i = 0; i < tb_Conexoes.getItems().size(); i++){
            Cliente cli = tb_Conexoes.getItems().get(i);
            
            Integer p = cli.getPorta();
            try {
                Socket skt = new Socket(cli.getIp(), p);//Unoeste
                //Socket skt = new Socket("127.0.0.1", 8082);
                output = new ObjectOutputStream(skt.getOutputStream());
                output.flush();
                output.writeObject(acceptRes.toString());
                output.close();
            } catch (IOException e) {
                System.out.println("Erro "+e.getMessage());
            }
        }
    }
}
