import java.util.Comparator;

/**
 * Ordenação por Inserção (Insertion sort).
 * Percorre o vetor da esquerda para a direita; cada elemento é retirado e inserido
 * na posição correta da parte já ordenada (à sua esquerda), deslocando os maiores uma posição.
 * Melhor caso (dados já ordenados): n-1 comparações, O(n).
 * Pior caso (dados em ordem inversa): n(n-1)/2 comparações, O(n²).
 */
public class Insertionsort<T extends Comparable<T>> implements IOrdenator<T> {

	private T[] dadosOrdenados;
	private Comparator<T> comparador;
	private long comparacoes;
	private long movimentacoes;
	private long inicio;
	private long termino;

	public Insertionsort() {
		comparacoes = 0;
		movimentacoes = 0;
		setComparador(T::compareTo);
	}

	public Insertionsort(Comparator<T> comparador) {
		comparacoes = 0;
		movimentacoes = 0;
		setComparador(comparador);
	}

	@Override
	public void setComparador(Comparator<T> comparador) {
		this.comparador = comparador;
	}

	@Override
	public T[] ordenar(T[] dados) {

		dadosOrdenados = dados;

		comparacoes = 0;
		movimentacoes = 0;
		iniciar();

		for (int i = 1; i < dadosOrdenados.length; i++) {
			T atual = dadosOrdenados[i];
			int j = i - 1;

			// Desloca para a direita os elementos maiores que o atual
			while (j >= 0) {
				comparacoes++;
				if (comparador.compare(dadosOrdenados[j], atual) > 0) {
					dadosOrdenados[j + 1] = dadosOrdenados[j];
					movimentacoes++;
					j--;
				} else {
					break;
				}
			}

			// Insere o atual na posição liberada
			dadosOrdenados[j + 1] = atual;
			movimentacoes++;
		}

		terminar();

		return dadosOrdenados;
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public long getMovimentacoes() {
		return movimentacoes;
	}

	private void iniciar() {
		inicio = System.nanoTime();
	}

	private void terminar() {
		termino = System.nanoTime();
	}

	@Override
	public double getTempoOrdenacao() {

		double tempoTotal;

		tempoTotal = (termino - inicio) / 1_000_000.0;
		return tempoTotal;
	}
}
