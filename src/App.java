import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** Pilha de produtos mais recentemente pedidos */
    static Pilha<Produto> pilhaProdutosRecentes = new Pilha<>();

    /** Nome do arquivo de persistência dos pedidos */
    static String nomeArquivoPedidos = "pedidos.txt";
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Listar os dígitos do número de matrícula");
        System.out.println("8 - Testar subPilha (K produtos mais recentes)");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		pedido.incluirProduto(produto);
        	}
        }
    	
    	return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado em uma pilha de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
    	if (pedido == null) {
    		System.out.println("Nenhum pedido em andamento para finalizar.");
    		return;
    	}
    	pilhaPedidos.empilhar(pedido);
    	Produto[] produtos = pedido.getProdutos();
    	for (int i = 0; i < pedido.getQuantosProdutos(); i++) {
    		pilhaProdutosRecentes.empilhar(produtos[i]);
    	}
    	System.out.println("Pedido finalizado com sucesso!");
    	System.out.println(pedido);
    }

    public static void listarProdutosPedidosRecentes() {
    	cabecalho();
    	System.out.println("PRODUTOS DOS PEDIDOS MAIS RECENTES:");
    	if (pilhaProdutosRecentes.vazia()) {
    		System.out.println("Nenhum produto nos pedidos recentes.");
    		return;
    	}
    	Pilha<Produto> temp = new Pilha<>();
    	int i = 1;
    	while (!pilhaProdutosRecentes.vazia()) {
    		Produto p = pilhaProdutosRecentes.desempilhar();
    		System.out.println(String.format("%02d - %s", i++, p));
    		temp.empilhar(p);
    	}
    	while (!temp.vazia()) {
    		pilhaProdutosRecentes.empilhar(temp.desempilhar());
    	}
    }

    static void salvarPedidos(String nomeArquivo) {
    	Pilha<Pedido> temp = new Pilha<>();
    	int count = 0;
    	while (!pilhaPedidos.vazia()) {
    		temp.empilhar(pilhaPedidos.desempilhar());
    		count++;
    	}
    	try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
    			new FileOutputStream(nomeArquivo), Charset.forName("UTF-8")))) {
    		writer.println(count);
    		while (!temp.vazia()) {
    			Pedido p = temp.desempilhar();
    			writer.println(p.gerarDadosTexto());
    			pilhaPedidos.empilhar(p);
    		}
    	} catch (IOException e) {
    		System.err.println("Erro ao salvar pedidos: " + e.getMessage());
    		while (!temp.vazia()) {
    			pilhaPedidos.empilhar(temp.desempilhar());
    		}
    	}
    }

    static void carregarPedidos(String nomeArquivo) {
    	Scanner arquivo = null;
    	try {
    		arquivo = new Scanner(new File(nomeArquivo), Charset.forName("UTF-8"));
    		int numPedidos = Integer.parseInt(arquivo.nextLine());
    		for (int i = 0; i < numPedidos; i++) {
    			Pedido pedido = Pedido.criarDoTexto(arquivo.nextLine(), produtosCadastrados, quantosProdutos);
    			pilhaPedidos.empilhar(pedido);
    			Produto[] prods = pedido.getProdutos();
    			for (int j = 0; j < pedido.getQuantosProdutos(); j++) {
    				pilhaProdutosRecentes.empilhar(prods[j]);
    			}
    		}
    	} catch (IOException e) {
    		// nada
    	} finally {
    		if (arquivo != null) arquivo.close();
    	}
    }
    
    public static void testarSubPilha() {
        cabecalho();
        System.out.println("TESTE DE subPilha");

        if (pilhaProdutosRecentes.vazia()) {
            System.out.println("A pilha de produtos recentes está vazia. Finalize ao menos um pedido antes de testar.");
            return;
        }

        System.out.println("\nConteúdo atual de pilhaProdutosRecentes (topo → fundo):");
        Pilha<Produto> temp = new Pilha<>();
        int total = 0;
        while (!pilhaProdutosRecentes.vazia()) {
            Produto p = pilhaProdutosRecentes.desempilhar();
            System.out.println(String.format("  %02d - %s", ++total, p));
            temp.empilhar(p);
        }
        while (!temp.vazia()) {
            pilhaProdutosRecentes.empilhar(temp.desempilhar());
        }

        int k = lerOpcao("\nDigite K (quantos elementos do topo deseja obter na subPilha): ", Integer.class);

        try {
            Pilha<Produto> sub = pilhaProdutosRecentes.subPilha(k);
            System.out.println("\nsubPilha com os " + k + " produtos do topo (topo → fundo):");
            int i = 1;
            while (!sub.vazia()) {
                System.out.println(String.format("  %02d - %s", i++, sub.desempilhar()));
            }

            System.out.println("\nVerificando que pilhaProdutosRecentes permanece inalterada (topo → fundo):");
            Pilha<Produto> temp2 = new Pilha<>();
            int j = 1;
            while (!pilhaProdutosRecentes.vazia()) {
                Produto p = pilhaProdutosRecentes.desempilhar();
                System.out.println(String.format("  %02d - %s", j++, p));
                temp2.empilhar(p);
            }
            while (!temp2.vazia()) {
                pilhaProdutosRecentes.empilhar(temp2.desempilhar());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public static void listarNumeroMatricula() {
        var pilha = new Pilha<Integer>();
        pilha.empilhar(7);
        pilha.empilhar(6);
        pilha.empilhar(1);
        pilha.empilhar(8);

        System.out.println("Pilha original:");
        while (!pilha.vazia()) {
            System.out.println(pilha.consultarTopo());
            pilha.desempilhar();
        }
    }
	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        carregarPedidos(nomeArquivoPedidos);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> { finalizarPedido(pedido); pedido = null; }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> listarNumeroMatricula();
                case 8 -> testarSubPilha();
            }
            pausa();
        }while(opcao != 0);

        salvarPedidos(nomeArquivoPedidos);
        teclado.close();    
    }
}
