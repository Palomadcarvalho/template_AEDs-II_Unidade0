import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class App {

    /** Nome do arquivo de dados (fica na pasta raiz do projeto). */
    static final String NOME_ARQUIVO = "produtos.txt";

    /** Vetor com os produtos cadastrados. É static para todos os métodos enxergarem. */
    static Produto[] produtos;

    /** Um único Scanner para o teclado, usado pelo programa inteiro. */
    static Scanner teclado = new Scanner(System.in);

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato:
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos.
     * Retorna um vetor vazio em caso de problemas com a leitura do arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        try (Scanner arquivo = new Scanner(new File(nomeArquivoDados), "UTF-8")) {
            int quantidade = Integer.parseInt(arquivo.nextLine().trim());
            Produto[] lidos = new Produto[quantidade];
            int validos = 0;

            for (int i = 0; i < quantidade; i++) {
                String linha = arquivo.nextLine();
                try {
                    lidos[validos] = Produto.criarDoTexto(linha);
                    validos++;
                } catch (RuntimeException e) {
                    // Uma linha com problema (ex.: perecível já vencido) é ignorada,
                    // para não perder os outros produtos do arquivo.
                    System.out.println("Linha ignorada (" + e.getMessage() + "): " + linha);
                }
            }

            // Copia só os produtos válidos para um vetor do tamanho certo
            Produto[] resultado = new Produto[validos];
            for (int i = 0; i < validos; i++) {
                resultado[i] = lidos[i];
            }
            return resultado;

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo " + nomeArquivoDados + " não encontrado. Começando sem produtos.");
            return new Produto[0];
        } catch (RuntimeException e) {
            System.out.println("Erro ao ler o arquivo " + nomeArquivoDados + ". Começando sem produtos.");
            return new Produto[0];
        }
    }

    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário,
     * e imprime seus dados.
     * A busca não é sensível ao caso. No caso de não encontrar o produto, imprime uma mensagem padrão */
    static void localizarProdutos() {
        System.out.print("Nome do produto: ");
        String nome = teclado.nextLine().trim();

        Produto procurado;
        try {
            // Produto "de mentira" só para usar o equals, que compara pela descrição
            procurado = new ProdutoNaoPerecivel(nome, 1.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Produto não encontrado.");
            return;
        }

        for (int i = 0; i < produtos.length; i++) {
            if (produtos[i].equals(procurado)) {
                System.out.println(produtos[i]);
                return;
            }
        }
        System.out.println("Produto não encontrado.");
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve todo o conteúdo do arquivo.
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    static void salvarProdutos(String nomeArquivo) {
        try (PrintWriter arquivo = new PrintWriter(nomeArquivo, "UTF-8")) {
            arquivo.println(produtos.length);
            for (int i = 0; i < produtos.length; i++) {
                arquivo.println(produtos[i].gerarDadosTexto());
            }
            System.out.println("Produtos salvos em " + nomeArquivo + ".");
        } catch (Exception e) {
            System.out.println("Erro ao salvar o arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
        if (produtos.length == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (int i = 0; i < produtos.length; i++) {
            try {
                System.out.println((i + 1) + " - " + produtos[i]);
            } catch (IllegalStateException e) {
                // toString chama valorVenda, que lança exceção se o perecível venceu
                System.out.println((i + 1) + " - " + produtos[i].getDescricao() + ": VENCIDO");
            }
        }
    }

    /**
     * Rotina para cadastro de um novo produto: pergunta ao usuário o tipo do produto, lê os dados correspondentes,
     * cria o objeto adequado de acordo com seu tipo, e inclui o produto no vetor.
     */
    static void cadastrarProduto() {
        try {
            System.out.print("Tipo (1 - Não perecível, 2 - Perecível): ");
            int tipo = Integer.parseInt(teclado.nextLine().trim());
            if (tipo != 1 && tipo != 2) {
                System.out.println("Tipo inválido.");
                return;
            }

            System.out.print("Descrição: ");
            String descricao = teclado.nextLine().trim();
            System.out.print("Preço de custo (ex.: 10.50): ");
            double precoCusto = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
            System.out.print("Margem de lucro (ex.: 0.20 para 20%): ");
            double margemLucro = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

            Produto novo;
            if (tipo == 1) {
                novo = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
            } else {
                System.out.print("Data de validade (dd/mm/aaaa): ");
                LocalDate validade = LocalDate.parse(teclado.nextLine().trim(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                novo = new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
            }

            // Vetor em Java tem tamanho fixo: cria um vetor uma posição maior,
            // copia os produtos antigos e coloca o novo na última posição.
            Produto[] maior = new Produto[produtos.length + 1];
            for (int i = 0; i < produtos.length; i++) {
                maior[i] = produtos[i];
            }
            maior[produtos.length] = novo;
            produtos = maior;

            System.out.println("Produto cadastrado: " + novo);
        } catch (Exception e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        produtos = lerProdutos(NOME_ARQUIVO);
        System.out.println(produtos.length + " produto(s) carregado(s).");

        int opcao;
        do {
            System.out.println();
            System.out.println("===== PRODUTOS =====");
            System.out.println("1 - Listar todos os produtos");
            System.out.println("2 - Localizar produto");
            System.out.println("3 - Cadastrar produto");
            System.out.println("0 - Sair (salva os dados)");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    listarTodosOsProdutos();
                    break;
                case 2:
                    localizarProdutos();
                    break;
                case 3:
                    cadastrarProduto();
                    break;
                case 0:
                    salvarProdutos(NOME_ARQUIVO);
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        teclado.close();
    }
}
