package com.example.banco;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes BASICOS da ContaBancaria.
 *
 * Estes testes alcancam alta cobertura de linha (~100%), mas possuem lacunas
 * que permitem que MUTANTES SOBREVIVAM durante o teste de mutacao.
 *
 * Isso demonstra que cobertura de codigo != qualidade dos testes.
 */
@DisplayName("Testes Basicos - ContaBancaria")
class ContaBancariaTest {

    private ContaBancaria conta;

    @BeforeEach
    void setUp() {
        conta = new ContaBancaria("Joao Silva", 1000.0, 500.0);
    }

    // ========== TESTES DE DEPOSITO ==========

    @Test
    @DisplayName("Deve depositar valor positivo com sucesso")
    void deveDepositarValorPositivo() {
        conta.depositar(500.0);
        assertEquals(1500.0, conta.getSaldo());
    }

    @Test
    @DisplayName("Deve rejeitar deposito com valor negativo")
    void deveRejeitarDepositoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-100.0));
    }

    // ========== TESTES DE SAQUE ==========

    @Test
    @DisplayName("Deve sacar valor dentro do saldo")
    void deveSacarDentroDoSaldo() {
        boolean resultado = conta.sacar(500.0);
        assertTrue(resultado);
        assertEquals(500.0, conta.getSaldo());
    }

    @Test
    @DisplayName("Deve recusar saque acima do limite")
    void deveRecusarSaqueAcimaDoLimite() {
        boolean resultado = conta.sacar(2000.0);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar saque com valor negativo")
    void deveRejeitarSaqueNegativo() {
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-50.0));
    }

    // ========== TESTES DE TRANSFERENCIA ==========

    @Test
    @DisplayName("Deve transferir entre contas com sucesso")
    void deveTransferirComSucesso() {
        ContaBancaria destino = new ContaBancaria("Maria Santos", 200.0, 0.0);
        conta.transferir(destino, 300.0);
        assertEquals(700.0, conta.getSaldo());
        assertEquals(500.0, destino.getSaldo());
    }

    @Test
    @DisplayName("Deve rejeitar transferencia para conta nula")
    void deveRejeitarTransferenciaContaNula() {
        assertThrows(IllegalArgumentException.class, () -> conta.transferir(null, 100.0));
    }

    // ========== TESTES DE JUROS ==========

    @Test
    @DisplayName("Deve calcular juros simples corretamente")
    void deveCalcularJuros() {
        double juros = conta.calcularJuros(0.12, 1);
        assertTrue(juros > 0);
    }

    @Test
    @DisplayName("Deve retornar zero juros para saldo zero")
    void deveRetornarZeroJurosParaSaldoZero() {
        ContaBancaria contaZerada = new ContaBancaria("Teste", 0.0, 0.0);
        // Nota: saldo == 0, condicao eh saldo <= 0
        // Este teste NAO mata o mutante boundary (saldo < 0 vs saldo <= 0)
        // porque nao testamos saldo == 0 separadamente com assertExact
    }

    // ========== TESTES DE STATUS ==========

    @Test
    @DisplayName("Deve retornar status ATIVA para conta com saldo positivo")
    void deveRetornarStatusAtiva() {
        assertEquals("ATIVA", conta.getStatusConta());
    }

    @Test
    @DisplayName("Deve retornar status INATIVA apos encerramento")
    void deveRetornarStatusInativa() {
        ContaBancaria contaZerada = new ContaBancaria("Teste", 0.0, 0.0);
        contaZerada.encerrarConta();
        assertEquals("INATIVA", contaZerada.getStatusConta());
    }

    // ========== TESTES DE PREMIUM ==========

    @Test
    @DisplayName("Deve identificar conta elegivel para premium")
    void deveIdentificarContaPremium() {
        ContaBancaria contaRica = new ContaBancaria("Rico", 30000.0, 0.0);
        assertTrue(contaRica.isElegivelParaPremium());
    }

    @Test
    @DisplayName("Deve identificar conta nao elegivel para premium")
    void deveIdentificarContaNaoPremium() {
        assertFalse(conta.isElegivelParaPremium());
    }

    // ========== TESTES DE CATEGORIA ==========

    @Test
    @DisplayName("Deve classificar conta Standard")
    void deveClassificarStandard() {
        assertEquals(CategoriaConta.STANDARD, conta.getCategoria());
    }

    @Test
    @DisplayName("Deve classificar conta Premium")
    void deveClassificarPremium() {
        ContaBancaria contaRica = new ContaBancaria("Rico", 30000.0, 0.0);
        assertEquals(CategoriaConta.PREMIUM, contaRica.getCategoria());
    }

    // ========== TESTES DE TAXA ==========

    @Test
    @DisplayName("Deve calcular taxa de manutencao para conta Standard")
    void deveCalcularTaxaStandard() {
        double taxa = conta.calcularTaxaManutencao();
        assertTrue(taxa > 0);
    }

    // ========== TESTES DE CONSTRUTOR ==========

    @Test
    @DisplayName("Deve rejeitar titular vazio")
    void deveRejeitarTitularVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContaBancaria("", 100.0, 0.0));
    }

    @Test
    @DisplayName("Deve rejeitar saldo inicial negativo")
    void deveRejeitarSaldoInicialNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContaBancaria("Teste", -100.0, 0.0));
    }

    // ========== TESTES DE ENCERRAMENTO ==========

    @Test
    @DisplayName("Deve encerrar conta com saldo zero")
    void deveEncerrarContaComSaldoZero() {
        ContaBancaria contaZerada = new ContaBancaria("Teste", 0.0, 0.0);
        contaZerada.encerrarConta();
        assertFalse(contaZerada.isAtiva());
    }

    @Test
    @DisplayName("Deve rejeitar encerramento com saldo diferente de zero")
    void deveRejeitarEncerramentoComSaldo() {
        assertThrows(IllegalStateException.class, () -> conta.encerrarConta());
    }
}
