import java.util.Comparator;

/**
 * Critério de ordenação: descrição do produto, em ordem alfabética,
 * sem diferenciar maiúsculas de minúsculas. Em caso de empate, usa o identificador.
 */
public class ComparadorPorDescricao implements Comparator<Produto> {

	@Override
	public int compare(Produto p1, Produto p2) {
		int resultado = p1.descricao.compareToIgnoreCase(p2.descricao);
		if (resultado == 0)
			resultado = p1.compareTo(p2);
		return resultado;
	}
}
