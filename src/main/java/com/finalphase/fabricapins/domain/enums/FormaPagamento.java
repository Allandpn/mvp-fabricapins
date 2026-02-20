package com.finalphase.fabricapins.domain.enums;

public enum FormaPagamento {
    PIX(1),
    CARTAO_CREDITO(2);

    private final int codigo;

    private FormaPagamento(int codigo){
        this.codigo = codigo;
    }

    public int getValue(){
        return codigo;
    }

    public static FormaPagamento valorDoCodigo(int codigo){
        for(FormaPagamento value : FormaPagamento.values()){
            if(value.getValue() == codigo){
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
