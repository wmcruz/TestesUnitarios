package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calculadoraMock; // mock realiza o teste da implementação(interface)

    @Spy
    private Calculadora calculadoraSpy; // spy realiza o teste da execução do método

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deveMostrarDiferencaEntreMockSpy() {
        Mockito.when(calculadoraMock.somar(1, 2)).thenReturn(8);
        // Mockito.when(calculadoraSpy.somar(1, 2)).thenReturn(8); // aqui o java vai executar o metodo somar, podendo ocorrer um nullpointer e etc..
        Mockito.doReturn(8).when(calculadoraSpy).somar(1, 2); // forma correta de utilizar o spy

        System.out.println("Mock: " + calculadoraMock.somar(1, 5));
        System.out.println("Spy: " + calculadoraSpy.somar(1, 5));
    }

    @Test
    public void testeDeMockParaCalculadoraSoma() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);
        Mockito.when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5); // testa a soma de qualquer valor inteiro com outro qualquer inteiro
        Mockito.when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5); // testa a soma de um valor fixo com outro qualquer inteiro

        Assert.assertEquals(5, calculadora.somar(1, 1000));
    }
}