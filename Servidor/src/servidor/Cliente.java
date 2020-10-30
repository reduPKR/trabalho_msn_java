package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {
    private String ip;
    private String nome;
    private int porta;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Cliente(String ip, String nome, int porta, Socket socket, ObjectOutputStream output, ObjectInputStream input) {
        this.ip = ip;
        this.nome = nome;
        this.porta = porta;
        this.socket = socket;
        this.output = output;
        this.input = input;
    }

    public Cliente(String ip, String nome, int porta) {
        this.ip = ip;
        this.nome = nome;
        this.porta = porta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
    
    
}
