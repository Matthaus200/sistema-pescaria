package br.univille.pescaria.config;

import br.univille.pescaria.model.Captura;
import br.univille.pescaria.model.Embarcacao;
import br.univille.pescaria.model.Especie;
import br.univille.pescaria.model.LocalPesca;
import br.univille.pescaria.model.Pescador;
import br.univille.pescaria.model.Pescaria;
import br.univille.pescaria.model.TipoLocal;
import br.univille.pescaria.repository.EmbarcacaoRepository;
import br.univille.pescaria.repository.EspecieRepository;
import br.univille.pescaria.repository.LocalPescaRepository;
import br.univille.pescaria.repository.PescadorRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cadastra dados de exemplo na primeira vez que o sistema roda com o banco vazio,
 * para as telas não começarem em branco na apresentação.
 *
 * Só roda quando TODAS as tabelas de cadastro estão vazias. Para desligar, use
 * pescaria.dados-exemplo=false no application.properties.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final boolean habilitado;
    private final PescadorRepository pescadorRepository;
    private final LocalPescaRepository localPescaRepository;
    private final EmbarcacaoRepository embarcacaoRepository;
    private final EspecieRepository especieRepository;
    private final PescariaRepository pescariaRepository;

    public DadosIniciais(@Value("${pescaria.dados-exemplo:true}") boolean habilitado,
                         PescadorRepository pescadorRepository,
                         LocalPescaRepository localPescaRepository,
                         EmbarcacaoRepository embarcacaoRepository,
                         EspecieRepository especieRepository,
                         PescariaRepository pescariaRepository) {
        this.habilitado = habilitado;
        this.pescadorRepository = pescadorRepository;
        this.localPescaRepository = localPescaRepository;
        this.embarcacaoRepository = embarcacaoRepository;
        this.especieRepository = especieRepository;
        this.pescariaRepository = pescariaRepository;
    }

    @Override
    public void run(String... args) {
        boolean bancoVazio = pescadorRepository.count() == 0
                && localPescaRepository.count() == 0
                && embarcacaoRepository.count() == 0
                && especieRepository.count() == 0;
        if (!habilitado || !bancoVazio) {
            return;
        }
        log.info("Banco vazio: cadastrando dados de exemplo.");

        Pescador matthaus = pescadorRepository.save(pescador("Matthäus de Paula", "111.222.333-44", "matthaus@email.com", "(47) 99999-1111", "2026-03-02"));
        Pescador joao = pescadorRepository.save(pescador("João Gabriel Nasario", "222.333.444-55", "joao@email.com", "(47) 99999-2222", "2026-03-02"));
        Pescador nicolas = pescadorRepository.save(pescador("Nicolas Rodrigues", "333.444.555-66", "nicolas@email.com", "(47) 99999-3333", "2026-04-11"));
        Pescador rafaela = pescadorRepository.save(pescador("Rafaela Gusczak", "444.555.666-77", "rafaela@email.com", "(47) 99999-4444", "2026-05-20"));

        LocalPesca cubatao = localPescaRepository.save(local("Rio Cubatão Norte", TipoLocal.RIO, "Joinville", "Margem acessível, bom para tilápia."));
        LocalPesca cascata = localPescaRepository.save(local("Lago da Cascata", TipoLocal.LAGO, "Joinville", "Lago com píer e área de descanso."));
        LocalPesca represa = localPescaRepository.save(local("Represa Bonita", TipoLocal.REPRESA, "Schroeder", "Pesca de barranco, água calma."));
        LocalPesca itapoa = localPescaRepository.save(local("Mar de Itapoá", TipoLocal.MAR, "Itapoá", "Pesca de arremesso na praia."));
        localPescaRepository.save(local("Três Lagoas", TipoLocal.PESQUE_PAGUE, "Guaramirim", "Pesque e solte para toda a família."));

        Embarcacao tainhaI = embarcacaoRepository.save(embarcacao("Tainha I", "Barco a motor", 4, "BR-0012"));
        Embarcacao caiaque = embarcacaoRepository.save(embarcacao("Caiaque Azul", "Caiaque", 1, null));
        embarcacaoRepository.save(embarcacao("Bote Santa Rita", "Bote", 3, "BR-0345"));

        Especie tilapia = especieRepository.save(especie("Tilápia", "Oreochromis niloticus", 25.0, null));
        Especie tainha = especieRepository.save(especie("Tainha", "Mugil liza", 35.0, "Maio a julho"));
        Especie robalo = especieRepository.save(especie("Robalo", "Centropomus parallelus", 46.0, "Maio a junho"));
        Especie traira = especieRepository.save(especie("Traíra", "Hoplias malabaricus", 25.0, "Nov. a fev."));
        Especie tucunare = especieRepository.save(especie("Tucunaré", "Cichla ocellaris", 30.0, null));

        Pescaria p1 = pescaria("2026-10-18T06:30", 4.5, "Nublado", "Saída cedo, água um pouco turva depois da chuva.", matthaus, cubatao, caiaque);
        p1.adicionarCaptura(captura(tilapia, 3, 2.4, 28.0, false));
        p1.adicionarCaptura(captura(traira, 1, 1.1, 34.0, true));
        p1.adicionarCaptura(captura(tucunare, 1, 0.9, 31.0, true));
        pescariaRepository.save(p1);

        Pescaria p2 = pescaria("2026-10-10T07:00", 3.0, "Ensolarado", null, rafaela, represa, null);
        p2.adicionarCaptura(captura(tilapia, 3, 2.0, 26.0, false));
        pescariaRepository.save(p2);

        Pescaria p3 = pescaria("2026-10-03T05:45", 6.0, "Vento forte", "Mar agitado no fim da manhã.", joao, itapoa, tainhaI);
        p3.adicionarCaptura(captura(robalo, 2, 5.8, 52.0, false));
        p3.adicionarCaptura(captura(tainha, 6, 7.2, 38.0, false));
        pescariaRepository.save(p3);

        Pescaria p4 = pescaria("2026-09-27T16:00", 2.0, "Chuvoso", null, nicolas, cascata, null);
        p4.adicionarCaptura(captura(traira, 2, 1.6, 22.0, true));
        pescariaRepository.save(p4);
    }

    private static Pescador pescador(String nome, String cpf, String email, String telefone, String dataCadastro) {
        Pescador p = new Pescador();
        p.setNome(nome);
        p.setCpf(cpf);
        p.setEmail(email);
        p.setTelefone(telefone);
        p.setDataCadastro(LocalDate.parse(dataCadastro));
        return p;
    }

    private static LocalPesca local(String nome, TipoLocal tipo, String cidade, String descricao) {
        LocalPesca l = new LocalPesca();
        l.setNome(nome);
        l.setTipo(tipo);
        l.setCidade(cidade);
        l.setEstado("SC");
        l.setDescricao(descricao);
        return l;
    }

    private static Embarcacao embarcacao(String nome, String tipo, int capacidade, String registro) {
        Embarcacao e = new Embarcacao();
        e.setNome(nome);
        e.setTipo(tipo);
        e.setCapacidade(capacidade);
        e.setRegistro(registro);
        return e;
    }

    private static Especie especie(String nomePopular, String nomeCientifico, double tamanhoMinimoCm, String defeso) {
        Especie e = new Especie();
        e.setNomePopular(nomePopular);
        e.setNomeCientifico(nomeCientifico);
        e.setTamanhoMinimoCm(tamanhoMinimoCm);
        e.setPeriodoDefeso(defeso);
        return e;
    }

    private static Pescaria pescaria(String dataHora, double duracao, String clima, String observacoes,
                                     Pescador pescador, LocalPesca local, Embarcacao embarcacao) {
        Pescaria p = new Pescaria();
        p.setDataHora(LocalDateTime.parse(dataHora));
        p.setDuracaoHoras(duracao);
        p.setClima(clima);
        p.setObservacoes(observacoes);
        p.setPescador(pescador);
        p.setLocalPesca(local);
        p.setEmbarcacao(embarcacao);
        return p;
    }

    private static Captura captura(Especie especie, int quantidade, double pesoKg, double comprimentoCm, boolean devolvido) {
        Captura c = new Captura();
        c.setEspecie(especie);
        c.setQuantidade(quantidade);
        c.setPesoKg(pesoKg);
        c.setComprimentoCm(comprimentoCm);
        c.setDevolvidoAgua(devolvido);
        return c;
    }
}
