import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private LocalDate dataDeValidade;

    /**
     * Construtor do produto perecível.
     * @param desc Descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto Preço de custo (mínimo 0.01)
     * @param margemLucro Margem de lucro (mínimo 0.01)
     * @param validade Data de validade (não pode ser anterior a hoje)
     */
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);
        if (validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior a hoje.");
        }
        this.dataDeValidade = validade;
    }

    /**
     * Valor de venda: lança exceção se vencido; aplica 25% de desconto
     * se faltarem 7 dias ou menos para o vencimento.
     * @return Valor de venda do produto
     */
    @Override
    public double valorVenda() {
        LocalDate hoje = LocalDate.now();

        if (dataDeValidade.isBefore(hoje)) {
            throw new IllegalStateException("Produto vencido não pode ser vendido.");
        }

        double precoNormal = precoCusto * (1.0 + margemLucro);
        long diasParaVencer = ChronoUnit.DAYS.between(hoje, dataDeValidade);

        if (diasParaVencer <= PRAZO_DESCONTO) {
            return precoNormal * (1.0 - DESCONTO);
        }
        return precoNormal;
    }

    /**
     * @return String no formato: NOME: [descrição]: [valor de venda] - Validade: dd/MM/aaaa
     */
    @Override
    public String toString() {
        return super.toString() + " - Validade: " + dataDeValidade.format(FORMATO_DATA);
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto.
     * Preço e margem de lucro são formatados com 2 casas decimais.
     * Data de validade é formatada no formato dd/mm/aaaa
     * @return Uma string no formato "2;descrição;preçoDeCusto;margemDeLucro;dataDeValidade"
     */
    @Override
    public String gerarDadosTexto() {
        return String.format(Locale.US, "2;%s;%.2f;%.2f;%s",
                getDescricao(), precoCusto, margemLucro, dataDeValidade.format(FORMATO_DATA));
    }
}
