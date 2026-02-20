package com.finalphase.fabricapins.domain.enums;

public enum TipoEstoqueProduto {
    ESTOQUE(1),
    PRODUCAO(2);

    private final int codigo;

    private TipoEstoqueProduto(int codigo){
        this.codigo = codigo;
    }

    public int getCodigo(){
        return codigo;
    }

    public TipoEstoqueProduto valorDoCodigo(int codigo){
        for(TipoEstoqueProduto value : TipoEstoqueProduto.values()){
            if(value.getCodigo() ==  codigo){
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
