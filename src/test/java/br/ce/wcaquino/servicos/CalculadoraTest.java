package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZero;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {

    Calculadora calculadora;
    @Before
    public void setup() {
        this.calculadora = new Calculadora();
    }

    @Test
    public void somarDoisValores() {
        int a = 5;
        int b = 3;

        int c = this.calculadora.somar(a, b);

        Assert.assertEquals(8, c);
    }

    @Test
    public void subtrairDoisValores() {
        int a = 5;
        int b = 3;

        int c = this.calculadora.subtrair(a, b);

        Assert.assertEquals(2, c);
    }

    @Test
    public void dividirDoisValores() throws NaoPodeDividirPorZero {
        int a = 6;
        int b = 3;

        int c = this.calculadora.dividir(a, b);

        Assert.assertEquals(2, c);
    }

    @Test(expected = NaoPodeDividirPorZero.class)
    public void dividirValorPorZero() throws NaoPodeDividirPorZero {
        int a = 10;
        int b = 0;

        int c = this.calculadora.dividir(a, b);
    }
}