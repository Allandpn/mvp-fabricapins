package com.finalphase.fabricapins.domain.enums;

public enum TipoSituacaoProduto {
    ATIVO(1),
    INATIVO(2);

    private final int codigo;

    private TipoSituacaoProduto(int codigo){
        this.codigo = codigo;
    }

    public int getCodigo(){
        return codigo;
    }

    public TipoSituacaoProduto valorDoCodigo(int codigo){
        for(TipoSituacaoProduto value : TipoSituacaoProduto.values()){
            if(value.getCodigo() ==  codigo){
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
