package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void testeDeMockParaCalculadoraSoma() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);
        Mockito.when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5); // testa a soma de qualquer valor inteiro com outro qualquer inteiro
        Mockito.when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5); // testa a soma de um valor fixo com outro qualquer inteiro

        System.out.println(calculadora.somar(1, 1000));
    }
}