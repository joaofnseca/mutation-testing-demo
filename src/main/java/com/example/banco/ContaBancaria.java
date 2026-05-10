package com.example.banco;

public class ContaBancaria {

    private String titular;
    private double saldo;
    private double limiteChequeEspecial;
    private boolean ativa;

    public ContaBancaria(String titular, double saldoInicial, double limiteChequeEspecial) {
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("Titular nao pode ser vazio");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("Saldo inicial nao pode ser negativo");
        }
        if (limiteChequeEspecial < 0) {
            throw new IllegalArgumentException("Limite do cheque especial nao pode ser negativo");
        }
        this.titular = titular;
        this.saldo = saldoInicial;
        this.limiteChequeEspecial = limiteChequeEspecial;
        this.ativa = true;
    }

    public void depositar(double valor) {
        verificarContaAtiva();
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do deposito deve ser positivo");
        }
        saldo += valor;
    }

    public boolean sacar(double valor) {
        verificarContaAtiva();
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser positivo");
        }
        if (saldo - valor < -limiteChequeEspecial) {
            return false;
        }
        saldo -= valor;
        return true;
    }

    public void transferir(ContaBancaria destino, double valor) {
        verificarContaAtiva();
        if (destino == null) {
            throw new IllegalArgumentException("Conta destino nao pode ser nula");
        }
        if (!destino.isAtiva()) {
            throw new IllegalStateException("Conta destino esta inativa");
        }
        if (!this.sacar(valor)) {
            throw new IllegalStateException("Saldo insuficiente para transferencia");
        }
        destino.depositar(valor);
    }

    public double calcularJuros(double taxaAnual, int meses) {
        if (taxaAnual < 0 || taxaAnual > 1) {
            throw new IllegalArgumentException("Taxa anual deve estar entre 0 e 1");
        }
        if (meses <= 0) {
            throw new IllegalArgumentException("Meses deve ser positivo");
        }
        if (saldo <= 0) {
            return 0.0;
        }
        double taxaMensal = taxaAnual / 12;
        return saldo * taxaMensal * meses;
    }

    public double calcularRendimentoComposto(double taxaAnual, int meses) {
        if (taxaAnual < 0 || taxaAnual > 1) {
            throw new IllegalArgumentException("Taxa anual deve estar entre 0 e 1");
        }
        if (meses <= 0) {
            throw new IllegalArgumentException("Meses deve ser positivo");
        }
        if (saldo <= 0) {
            return 0.0;
        }
        double taxaMensal = taxaAnual / 12;
        double montante = saldo * Math.pow(1 + taxaMensal, meses);
        return montante - saldo;
    }

    public boolean isElegivelParaPremium() {
        return ativa && saldo >= 25000;
    }

    public CategoriaConta getCategoria() {
        return CategoriaConta.classificar(saldo);
    }

    public String getStatusConta() {
        if (!ativa) {
            return "INATIVA";
        }
        if (saldo < 0) {
            return "CHEQUE_ESPECIAL";
        }
        if (saldo == 0) {
            return "ZERADA";
        }
        return "ATIVA";
    }

    public double calcularTaxaManutencao() {
        if (!ativa) {
            return 0.0;
        }
        CategoriaConta categoria = getCategoria();
        double taxaBase = 25.0;

        if (categoria == CategoriaConta.PREMIUM) {
            return 0.0;
        } else if (categoria == CategoriaConta.GOLD) {
            return taxaBase * 0.5;
        }
        return taxaBase;
    }

    public void encerrarConta() {
        if (saldo != 0) {
            throw new IllegalStateException("Conta deve ter saldo zero para ser encerrada");
        }
        this.ativa = false;
    }

    private void verificarContaAtiva() {
        if (!ativa) {
            throw new IllegalStateException("Operacao nao permitida em conta inativa");
        }
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
