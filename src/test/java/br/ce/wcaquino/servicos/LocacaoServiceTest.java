package br.ce.wcaquino.servicos;


import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    private LocacaoService service;

    private LocacaoDAO dao;

    private SPCService spcService;

    private EmailService emailService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        this.service = new LocacaoService();
        this.dao = Mockito.mock(LocacaoDAO.class);
        this.spcService = Mockito.mock(SPCService.class);
        this.emailService = Mockito.mock(EmailService.class);

        service.setLocacaoDAO(dao);
        service.setSpcService(spcService);
        service.setEmailService(emailService);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilme().comValor(5.0).agora());

        // acao
        Locacao locacao = service.alugarFilme(usuario, filme);

        // verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));
    }

    @Test(expected = FilmeSemEstoqueException.class) // formaElegante
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilme().comValor(5.0).semEstoque().agora());

        // acao
        service.alugarFilme(usuario, filme);
    }

    @Test //robusta
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        // cenario
        List<Filme> filme = Arrays.asList(umFilme().agora());

        // acao
        try {
            service.alugarFilme(null, filme);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
    }

    @Test //formaNova
    public void naoDeveAlugarFilmeSemFilme() throws LocadoraException, FilmeSemEstoqueException {
        // cenario
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        // acao
        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // verificacao
        assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
        assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadosSPC() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        exception.expect(LocadoraException.class);
        exception.expectMessage("Usuário Negativado");

        // acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Locacao> locacoes = Arrays.asList(
                LocacaoBuilder.umLocacao()
                            .comUsuario(usuario)
                            .comDataRetorno(DataUtils.obterDataComDiferencaDias(-2))
                        .agora());
        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtrasos();

        // verificacao
        Mockito.verify(emailService).notificarAtraso(usuario);
    }
}