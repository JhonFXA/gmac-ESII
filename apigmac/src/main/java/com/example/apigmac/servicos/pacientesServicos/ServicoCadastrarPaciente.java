package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioEndereco;
import com.example.apigmac.repositorios.RepositorioLogCadastroPaciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.documentacaoServicos.ServicoTransformarDocumentacao;
import com.example.apigmac.utils.ServicoVerificacao;
import com.example.apigmac.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.NoSuchElementException;

/**
 * Serviço responsável pelo cadastro de pacientes,
 * incluindo validações, endereços, documentos e auditoria do cadastro.
 */
@Service
public class ServicoCadastrarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    private RepositorioLogCadastroPaciente repositorioLogCadastroPaciente;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private ServicoVerificacao verificacao;

    @Autowired
    private ServicoTransformarDocumentacao transformarDocumentacao;

    @Autowired
    private RepositorioEndereco repositorioEndereco;

    /**
     * Realiza o cadastro completo de um paciente,
     * aplicando validações e persistindo informações associadas.
     */
    @Transactional
    public Paciente cadastrarPaciente(PacienteDTO dados, MultipartFile documento) {

        // Garante a existência dos dados obrigatórios
        if (dados == null) {
            throw new IllegalArgumentException("Dados do paciente não informados");
        }

        // Normaliza o CPF para padronização e validação
        String cpfNormalizado = CpfUtils.normalizar((dados.cpf()));

        // Validações de regras de negócio do paciente
        if (!verificacao.textoObrigatorioValido(dados.nome(), 3)) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (!verificacao.cpfValido(cpfNormalizado)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (repositorioPaciente.findByCpf(cpfNormalizado) != null) {
            throw new IllegalArgumentException("Paciente já cadastrado");
        }

        if (!verificacao.emailValido(dados.email())) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (repositorioPaciente.findByEmail(dados.email()) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (!verificacao.telefoneValido(dados.telefone())) {
            throw new IllegalArgumentException("Telefone inválido");
        }

        if (!verificacao.dataNascimentoValida(dados.dataNascimento())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }

        if (dados.sexo() == null) {
            throw new IllegalArgumentException("Sexo é obrigatório");
        }

        if (dados.estadoCivil() == null) {
            throw new IllegalArgumentException("Estado civil é obrigatório");
        }

        // Garante que ao menos um endereço seja informado
        if (dados.enderecos() == null || dados.enderecos().isEmpty()) {
            throw new IllegalArgumentException("É obrigatório informar pelo menos um endereço");
        }

        // Validação do documento, quando presente
        if (!verificacao.pdfValido(documento)) {
            throw new IllegalArgumentException("Documento inválido (apenas PDF)");
        }

        // Criação da entidade paciente com status inicial padrão
        Paciente paciente = new Paciente(
                dados.nome(),
                cpfNormalizado,
                dados.telefone(),
                dados.email(),
                dados.sexo(),
                dados.estadoCivil(),
                StatusSolicitacao.PENDENTE,
                dados.dataNascimento()
        );

        // Persistência do paciente
        paciente = repositorioPaciente.save(paciente);

        // Cadastro dos endereços associados ao paciente
        for (EnderecoDTO dto : dados.enderecos()) {
            cadastrarEndereco(dto, paciente.getCpf());
        }

        // Cadastro do documento, quando informado
        if (documento != null) {
            cadastrarDocumento(documento, paciente.getCpf());
        }

        // Registro de auditoria do cadastro (usuário e data)
        var authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        Cadastro cadastro = new Cadastro();
        cadastro.setPaciente(paciente);
        cadastro.setUsuario(usuarioLogado);
        cadastro.setDataCadastro(LocalDate.now());

        repositorioLogCadastroPaciente.save(cadastro);

        return paciente;
    }

    /**
     * Cadastra um endereço e associa ao paciente correspondente.
     */
    public void cadastrarEndereco(EnderecoDTO enderecoDTO, String cpf) {

        // Normaliza o CPF para busca consistente
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Validações dos dados de endereço
        if (enderecoDTO == null) {
            throw new IllegalArgumentException("Endereço não informado");
        }

        if (!verificacao.cepValido(enderecoDTO.cep())) {
            throw new IllegalArgumentException("CEP inválido");
        }

        if (!verificacao.estadoValido(enderecoDTO.estado())) {
            throw new IllegalArgumentException("Estado inválido");
        }

        if (!verificacao.textoObrigatorioValido(enderecoDTO.cidade(), 2)) {
            throw new IllegalArgumentException("Cidade inválida");
        }

        if (!verificacao.textoObrigatorioValido(enderecoDTO.bairro(), 2)) {
            throw new IllegalArgumentException("Bairro inválido");
        }

        if (!verificacao.textoObrigatorioValido(enderecoDTO.logradouro(), 3)) {
            throw new IllegalArgumentException("Logradouro inválido");
        }

        if (!verificacao.textoObrigatorioValido(enderecoDTO.numero(), 1)) {
            throw new IllegalArgumentException("Número inválido");
        }

        // Recupera o paciente para associação do endereço
        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        // Criação da entidade endereço
        Endereco endereco = new Endereco(
                enderecoDTO.cep(),
                enderecoDTO.cidade(),
                enderecoDTO.estado(),
                enderecoDTO.bairro(),
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento()
        );

        // Associação bidirecional paciente-endereço
        endereco.setPaciente(paciente);
        paciente.adicionarEndereco(endereco);

        repositorioEndereco.save(endereco);
    }

    /**
     * Cadastra um documento associado ao paciente,
     * definindo status inicial e metadados.
     */
    public void cadastrarDocumento(MultipartFile documento, String cpf) {

        // Garante a presença do documento
        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }

        // Normaliza o CPF para busca
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Validação do formato do documento
        if (!verificacao.pdfValido(documento)) {
            throw new IllegalArgumentException("Documento inválido (somente PDF)");
        }

        // Recupera o paciente para associação do documento
        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        // Geração do caminho de armazenamento do documento
        String caminho = transformarDocumentacao
                .caminhoDocumentacao(documento, paciente.getCpf());

        // Criação da entidade documentação
        Documentacao documentacao = new Documentacao();
        documentacao.setCaminho(caminho);
        documentacao.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
        documentacao.setDataEnvio(LocalDate.now());
        documentacao.setPaciente(paciente);

        // Associação e persistência do documento
        paciente.adicionarDocumentacao(documentacao);
        repositorioDocumentacao.save(documentacao);
    }
}
