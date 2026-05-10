# Mutation Testing Demo

Projeto da disciplina de Verificacao e Validacao de Software (PUCRS 2026/1) que demonstra testes de mutacao com [PIT (pitest)](https://pitest.org).

A ideia eh mostrar que cobertura de codigo alta nao garante testes bons — mutantes sobreviventes revelam onde os testes sao fracos.

## Como rodar

Precisa de Java 17+ e Maven 3.8+.

```bash
# testes unitarios
mvn test

# testes de mutacao
mvn org.pitest:pitest-maven:mutationCoverage

# com mutadores mais agressivos
mvn org.pitest:pitest-maven:mutationCoverage -Pmutation-full
```

O relatorio fica em `target/pit-reports/index.html`.

## Estrutura

- `ContaBancaria.java` — classe com operacoes bancarias (deposito, saque, transferencia, juros)
- `CategoriaConta.java` — enum com categorias de conta
- `ContaBancariaTest.java` — testes basicos, cobertura alta mas mutation score ~60-70%
- `ContaBancariaReforcoTest.java` — testes reforcados pra matar os mutantes que sobreviveram

## Resultado

Sem os testes reforcados, sobram ~25-30 mutantes vivos mesmo com ~95% de cobertura de linha. Com eles, o mutation score sobe pra ~95%+.
