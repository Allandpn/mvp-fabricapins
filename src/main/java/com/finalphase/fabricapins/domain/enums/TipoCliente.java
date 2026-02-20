package com.finalphase.fabricapins.domain.enums;

public enum TipoCliente {
    VAREJO(1),
    REVENDA(2);

    private final int codigo;

    private TipoCliente(int codigo){
        this.codigo = codigo;
    }

    public int getCodigo(){
        return this.codigo;
    }

    public static TipoCliente valorDoCodigo(int codigo){
        for (TipoCliente value : TipoCliente.values()){
            if(value.getCodigo() == codigo){
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido");
    }
}
