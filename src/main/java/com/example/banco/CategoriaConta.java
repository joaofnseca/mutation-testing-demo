package com.example.banco;

public enum CategoriaConta {

    STANDARD("Standard", 0.0, 0.5),
    GOLD("Gold", 5000.0, 0.75),
    PREMIUM("Premium", 25000.0, 1.0);

    private final String descricao;
    private final double saldoMinimo;
    private final double multiplicadorBeneficios;

    CategoriaConta(String descricao, double saldoMinimo, double multiplicadorBeneficios) {
        this.descricao = descricao;
        this.saldoMinimo = saldoMinimo;
        this.multiplicadorBeneficios = multiplicadorBeneficios;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getSaldoMinimo() {
        return saldoMinimo;
    }

    public double getMultiplicadorBeneficios() {
        return multiplicadorBeneficios;
    }

    public static CategoriaConta classificar(double saldo) {
        if (saldo >= PREMIUM.saldoMinimo) {
            return PREMIUM;
        } else if (saldo >= GOLD.saldoMinimo) {
            return GOLD;
        }
        return STANDARD;
    }
}
