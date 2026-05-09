public class TesteFila {

	public static void main(String[] args) {

		Fila<Character> filaCaracteres = new Fila<Character>();
		String primeiroNome = "Péricles";
		String segundoNome = "Pires";
		String nomeCompleto = primeiroNome + segundoNome;

		for (int i = 0; i < nomeCompleto.length(); i++) {
			filaCaracteres.enfileirar(nomeCompleto.charAt(i));
		}

		char caractereProcurado = 'e';
		System.out.println("Ocorrencias de '" + caractereProcurado + "': "
				+ filaCaracteres.contarOcorrencias(caractereProcurado));

		System.out.println("Caracteres desenfileirados:");
		while (!filaCaracteres.vazia()) {
			System.out.print(filaCaracteres.desenfileirar() + " ");
		}
		System.out.println();
		System.out.println("Fila vazia: " + filaCaracteres.vazia());
	}
}
