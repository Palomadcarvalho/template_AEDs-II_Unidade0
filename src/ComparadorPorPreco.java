import java.util.Comparator;

/**
 * Critério de ordenação: valor de venda do produto, do menor para o maior.
 * Em caso de empate, usa a descrição.
 */
public class ComparadorPorPreco implements Comparator<Produto> {

	@Override
	public int compare(Produto p1, Produto p2) {
		int resultado = Double.compare(p1.valorDeVenda(), p2.valorDeVenda());
		if (resultado == 0)
			resultado = p1.descricao.compareToIgnoreCase(p2.descricao);
		return resultado;
	}
}
