package cliente;

public class Mensagem {
    private ctrCliente origem;
    private ctrCliente destino;
    private String msg;

    public Mensagem(ctrCliente origem, ctrCliente destino, String msg) {
        this.origem = origem;
        this.destino = destino;
        this.msg = msg;
    }

    public ctrCliente getOrigem() {
        return origem;
    }

    public void setOrigem(ctrCliente origem) {
        this.origem = origem;
    }

    public ctrCliente getDestino() {
        return destino;
    }

    public void setDestino(ctrCliente destino) {
        this.destino = destino;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
