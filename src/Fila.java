import java.util.NoSuchElementException;
import java.util.Objects;

public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;

	public Fila() {

		Celula<E> sentinela = new Celula<E>();
		frente = sentinela;
		tras = sentinela;
	}

	public boolean vazia() {
		return frente == tras;
	}

	public void enfileirar(E item) {

		Celula<E> novaCelula = new Celula<E>(item);
		tras.setProximo(novaCelula);
		tras = novaCelula;
	}

	public E desenfileirar() {

		E desenfileirado = consultarFrente();
		frente = frente.getProximo();

		if (frente.getProximo() == null) {
			tras = frente;
		}

		return desenfileirado;
	}

	public E consultarFrente() {

		if (vazia()) {
			throw new NoSuchElementException("Nao ha nenhum item na fila!");
		}

		return frente.getProximo().getItem();
	}

	public int contarOcorrencias(E item) {

		int ocorrencias = 0;
		Celula<E> atual = frente.getProximo();

		while (atual != null) {
			if (Objects.equals(atual.getItem(), item)) {
				ocorrencias++;
			}
			atual = atual.getProximo();
		}

		return ocorrencias;
	}
}
