package com.finalphase.fabricapins.domain.enums;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO(1),
    PAGAMENTO_CONFIRMADO(2),
    EM_PRODUCAO(3),
    EM_SEPARACAO(4),
    AGUARDANDO_ENVIO(5),
    ENVIADO(6),
    ENTREGUE(7),
    CANCELADO(8),
    REEMBOLSADO(9);

    private final int codigo;

    private StatusPedido(int codigo){
        this.codigo = codigo;
    }

    public int getCodigo(){
        return this.codigo;
    }

    public static StatusPedido valorDoCodigo(int codigo){
        for (StatusPedido value : StatusPedido.values()){
            if(value.getCodigo() == codigo){
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
