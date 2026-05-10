package com.example.banco;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes REFORCADOS da ContaBancaria.
 *
 * Estes testes foram escritos APOS a analise dos mutantes sobreviventes
 * no relatorio do PIT. Cada teste aqui tem como objetivo matar um mutante
 * especifico que sobreviveu aos testes basicos.
 */
@DisplayName("Testes Reforcados - Matando Mutantes")
class ContaBancariaReforcoTest {

    private ContaBancaria conta;

    @BeforeEach
    void setUp() {
        conta = new ContaBancaria("Ana Costa", 1000.0, 500.0);
    }

    @Nested
    @DisplayName("Mutantes de Fronteira (Conditionals Boundary)")
    class MutantesFronteira {

        @Test
        @DisplayName("Deposito com valor ZERO deve falhar (mata mutante: <= vira <)")
        void depositoZeroDeveFalhar() {
            assertThrows(IllegalArgumentException.class, () -> conta.depositar(0));
        }

        @Test
        @DisplayName("Saque com valor ZERO deve falhar (mata mutante: <= vira <)")
        void saqueZeroDeveFalhar() {
            assertThrows(IllegalArgumentException.class, () -> conta.sacar(0));
        }

        @Test
        @DisplayName("Saque no limite EXATO do cheque especial deve funcionar")
        void saqueNoLimiteExato() {
            // saldo=1000, limite=500 -> pode sacar ate 1500
            assertTrue(conta.sacar(1500.0));
            assertEquals(-500.0, conta.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Saque 1 centavo acima do limite deve falhar")
        void saqueUmCentavoAcimaDoLimite() {
            assertFalse(conta.sacar(1500.01));
            assertEquals(1000.0, conta.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Premium com saldo EXATAMENTE 25000 deve ser elegivel")
        void premiumNoLimiteExato() {
            ContaBancaria contaLimite = new ContaBancaria("Limite", 25000.0, 0.0);
            assertTrue(contaLimite.isElegivelParaPremium());
        }

        @Test
        @DisplayName("Premium com saldo 24999.99 NAO deve ser elegivel")
        void premiumAbaixoDoLimite() {
            ContaBancaria contaAbaixo = new ContaBancaria("Abaixo", 24999.99, 0.0);
            assertFalse(contaAbaixo.isElegivelParaPremium());
        }

        @Test
        @DisplayName("Juros com saldo EXATAMENTE zero deve retornar zero")
        void jurosComSaldoZero() {
            ContaBancaria contaZerada = new ContaBancaria("Zero", 0.0, 0.0);
            assertEquals(0.0, contaZerada.calcularJuros(0.12, 6));
        }

        @Test
        @DisplayName("Classificacao Gold no limite exato de 5000")
        void classificacaoGoldNoLimite() {
            ContaBancaria contaGold = new ContaBancaria("Gold", 5000.0, 0.0);
            assertEquals(CategoriaConta.GOLD, contaGold.getCategoria());
        }

        @Test
        @DisplayName("Classificacao Standard com 4999.99")
        void classificacaoAbaixoGold() {
            ContaBancaria contaStd = new ContaBancaria("Std", 4999.99, 0.0);
            assertEquals(CategoriaConta.STANDARD, contaStd.getCategoria());
        }

        @Test
        @DisplayName("Classificacao Premium no limite exato de 25000")
        void classificacaoPremiumNoLimite() {
            ContaBancaria contaPremium = new ContaBancaria("Premium", 25000.0, 0.0);
            assertEquals(CategoriaConta.PREMIUM, contaPremium.getCategoria());
        }
    }

    @Nested
    @DisplayName("Mutantes Matematicos (Math Mutator)")
    class MutantesMatematicos {

        @Test
        @DisplayName("Juros simples: valor preciso (mata mutante: + vira -, * vira /)")
        void jurosValorPreciso() {
            // saldo=1000, taxa=0.12, meses=3
            // esperado = 1000 * (0.12/12) * 3 = 1000 * 0.01 * 3 = 30.0
            assertEquals(30.0, conta.calcularJuros(0.12, 3), 0.001);
        }

        @Test
        @DisplayName("Juros: verificar divisao por 12 (mata mutante: / vira *)")
        void jurosDivisaoPor12() {
            // saldo=1200, taxa=0.12, meses=1
            // esperado = 1200 * (0.12/12) * 1 = 1200 * 0.01 = 12.0
            ContaBancaria conta1200 = new ContaBancaria("Teste", 1200.0, 0.0);
            assertEquals(12.0, conta1200.calcularJuros(0.12, 1), 0.001);
        }

        @Test
        @DisplayName("Rendimento composto: valor preciso")
        void rendimentoCompostoValorPreciso() {
            // saldo=1000, taxa=0.12, meses=12
            // taxaMensal = 0.01
            // montante = 1000 * (1.01)^12 = 1000 * 1.12682503... = 1126.825...
            // rendimento = 126.825...
            double rendimento = conta.calcularRendimentoComposto(0.12, 12);
            assertEquals(126.825, rendimento, 0.01);
        }

        @Test
        @DisplayName("Deposito incrementa saldo (mata mutante: += vira -=)")
        void depositoIncrementaSaldo() {
            double saldoAntes = conta.getSaldo();
            conta.depositar(200.0);
            assertEquals(saldoAntes + 200.0, conta.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Saque decrementa saldo (mata mutante: -= vira +=)")
        void saqueDecrementaSaldo() {
            double saldoAntes = conta.getSaldo();
            conta.sacar(200.0);
            assertEquals(saldoAntes - 200.0, conta.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Taxa manutencao Gold eh metade da base (mata mutante: * vira /)")
        void taxaManutencaoGold() {
            ContaBancaria contaGold = new ContaBancaria("Gold", 10000.0, 0.0);
            assertEquals(12.5, contaGold.calcularTaxaManutencao(), 0.001);
        }

        @Test
        @DisplayName("Taxa manutencao Standard eh 25.0")
        void taxaManutencaoStandard() {
            assertEquals(25.0, conta.calcularTaxaManutencao(), 0.001);
        }

        @Test
        @DisplayName("Taxa manutencao Premium eh zero")
        void taxaManutencaoPremium() {
            ContaBancaria contaPremium = new ContaBancaria("Premium", 30000.0, 0.0);
            assertEquals(0.0, contaPremium.calcularTaxaManutencao(), 0.001);
        }
    }

    @Nested
    @DisplayName("Mutantes de Negacao de Condicionais (Negate Conditionals)")
    class MutantesNegacao {

        @Test
        @DisplayName("Conta inativa nao permite deposito")
        void contaInativaNaoPermiteDeposito() {
            ContaBancaria contaInativa = new ContaBancaria("Inativa", 0.0, 0.0);
            contaInativa.encerrarConta();
            assertThrows(IllegalStateException.class, () -> contaInativa.depositar(100.0));
        }

        @Test
        @DisplayName("Conta inativa nao permite saque")
        void contaInativaNaoPermiteSaque() {
            ContaBancaria contaInativa = new ContaBancaria("Inativa", 0.0, 0.0);
            contaInativa.encerrarConta();
            assertThrows(IllegalStateException.class, () -> contaInativa.sacar(100.0));
        }

        @Test
        @DisplayName("Conta inativa NAO eh elegivel para premium")
        void contaInativaNaoEhPremium() {
            ContaBancaria contaRica = new ContaBancaria("Rica", 50000.0, 0.0);
            // Primeiro verifica que seria elegivel
            assertTrue(contaRica.isElegivelParaPremium());
            // Zera saldo e encerra
            contaRica.sacar(50000.0);
            contaRica.encerrarConta();
            // Agora nao eh elegivel (ativa=false)
            assertFalse(contaRica.isElegivelParaPremium());
        }

        @Test
        @DisplayName("Transferencia para conta inativa deve falhar")
        void transferenciParaContaInativa() {
            ContaBancaria destino = new ContaBancaria("Destino", 0.0, 0.0);
            destino.encerrarConta();
            assertThrows(IllegalStateException.class, () -> conta.transferir(destino, 100.0));
        }
    }

    @Nested
    @DisplayName("Mutantes de Retorno (Return Values)")
    class MutantesRetorno {

        @Test
        @DisplayName("getStatusConta retorna exatamente CHEQUE_ESPECIAL")
        void statusChequeEspecial() {
            conta.sacar(1200.0); // saldo fica -200
            assertEquals("CHEQUE_ESPECIAL", conta.getStatusConta());
        }

        @Test
        @DisplayName("getStatusConta retorna exatamente ZERADA")
        void statusZerada() {
            conta.sacar(1000.0); // saldo fica 0
            assertEquals("ZERADA", conta.getStatusConta());
        }

        @Test
        @DisplayName("getStatusConta retorna exatamente ATIVA")
        void statusAtiva() {
            assertEquals("ATIVA", conta.getStatusConta());
        }

        @Test
        @DisplayName("getStatusConta retorna exatamente INATIVA")
        void statusInativa() {
            ContaBancaria contaInativa = new ContaBancaria("X", 0.0, 0.0);
            contaInativa.encerrarConta();
            assertEquals("INATIVA", contaInativa.getStatusConta());
        }

        @Test
        @DisplayName("sacar retorna TRUE quando bem-sucedido")
        void sacarRetornaTrue() {
            assertTrue(conta.sacar(100.0));
        }

        @Test
        @DisplayName("sacar retorna FALSE quando sem saldo")
        void sacarRetornaFalse() {
            assertFalse(conta.sacar(9999.0));
        }

        @Test
        @DisplayName("calcularJuros retorna exatamente 0 para saldo negativo")
        void jurosRetornaZeroParaSaldoNegativo() {
            conta.sacar(1200.0); // saldo = -200
            assertEquals(0.0, conta.calcularJuros(0.12, 6), 0.001);
        }

        @Test
        @DisplayName("Rendimento composto retorna 0 para saldo zero")
        void rendimentoCompostoZero() {
            ContaBancaria contaZerada = new ContaBancaria("Zero", 0.0, 0.0);
            assertEquals(0.0, contaZerada.calcularRendimentoComposto(0.12, 6), 0.001);
        }
    }

    @Nested
    @DisplayName("Mutantes de Chamada Void (Void Method Calls)")
    class MutantesVoidMethod {

        @Test
        @DisplayName("Transferencia realmente credita a conta destino")
        void transferenciaCredita() {
            ContaBancaria destino = new ContaBancaria("Destino", 0.0, 0.0);
            conta.transferir(destino, 500.0);
            assertEquals(500.0, destino.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Transferencia realmente debita a conta origem")
        void transferenciaDebita() {
            ContaBancaria destino = new ContaBancaria("Destino", 0.0, 0.0);
            conta.transferir(destino, 300.0);
            assertEquals(700.0, conta.getSaldo(), 0.001);
        }
    }

    @Nested
    @DisplayName("Testes de Validacao de Parametros")
    class ValidacaoParametros {

        @Test
        @DisplayName("Juros com taxa negativa lanca excecao")
        void jurosComTaxaNegativa() {
            assertThrows(IllegalArgumentException.class, () -> conta.calcularJuros(-0.1, 6));
        }

        @Test
        @DisplayName("Juros com taxa acima de 1 lanca excecao")
        void jurosComTaxaAcimaDe1() {
            assertThrows(IllegalArgumentException.class, () -> conta.calcularJuros(1.5, 6));
        }

        @Test
        @DisplayName("Juros com meses zero lanca excecao")
        void jurosComMesesZero() {
            assertThrows(IllegalArgumentException.class, () -> conta.calcularJuros(0.12, 0));
        }

        @Test
        @DisplayName("Juros com meses negativo lanca excecao")
        void jurosComMesesNegativo() {
            assertThrows(IllegalArgumentException.class, () -> conta.calcularJuros(0.12, -1));
        }

        @Test
        @DisplayName("Limite cheque especial negativo no construtor lanca excecao")
        void limiteNegativoNoConstructor() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ContaBancaria("Teste", 100.0, -1.0));
        }

        @Test
        @DisplayName("Titular nulo no construtor lanca excecao")
        void titularNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ContaBancaria(null, 100.0, 0.0));
        }
    }

    @Nested
    @DisplayName("Testes da Enum CategoriaConta")
    class TestesCategoria {

        @Test
        @DisplayName("STANDARD tem multiplicador 0.5")
        void standardMultiplicador() {
            assertEquals(0.5, CategoriaConta.STANDARD.getMultiplicadorBeneficios(), 0.001);
        }

        @Test
        @DisplayName("GOLD tem multiplicador 0.75")
        void goldMultiplicador() {
            assertEquals(0.75, CategoriaConta.GOLD.getMultiplicadorBeneficios(), 0.001);
        }

        @Test
        @DisplayName("PREMIUM tem multiplicador 1.0")
        void premiumMultiplicador() {
            assertEquals(1.0, CategoriaConta.PREMIUM.getMultiplicadorBeneficios(), 0.001);
        }

        @Test
        @DisplayName("Descricoes das categorias estao corretas")
        void descricoesCategorias() {
            assertEquals("Standard", CategoriaConta.STANDARD.getDescricao());
            assertEquals("Gold", CategoriaConta.GOLD.getDescricao());
            assertEquals("Premium", CategoriaConta.PREMIUM.getDescricao());
        }

        @Test
        @DisplayName("Saldo minimo das categorias estao corretos")
        void saldoMinimoCategorias() {
            assertEquals(0.0, CategoriaConta.STANDARD.getSaldoMinimo(), 0.001);
            assertEquals(5000.0, CategoriaConta.GOLD.getSaldoMinimo(), 0.001);
            assertEquals(25000.0, CategoriaConta.PREMIUM.getSaldoMinimo(), 0.001);
        }
    }
}
