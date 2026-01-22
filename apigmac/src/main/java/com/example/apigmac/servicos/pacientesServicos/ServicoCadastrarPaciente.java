package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Endereco;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioEndereco;
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


@Service
public class ServicoCadastrarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private ServicoVerificacao verificacao;

    @Autowired
    private ServicoTransformarDocumentacao transformarDocumentacao;

    @Autowired
    private RepositorioEndereco repositorioEndereco;


    @Transactional
    public Paciente cadastrarPaciente(PacienteDTO dados, MultipartFile documento){

        if (dados == null) {
            throw new IllegalArgumentException("Dados do paciente não informados");
        }
        String cpfNormalizado = CpfUtils.normalizar((dados.cpf()));

        if (!verificacao.textoObrigatorioValido(dados.nome(), 3)) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (!verificacao.cpfValido(cpfNormalizado)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (repositorioPaciente.findByCpf(cpfNormalizado) != null) {
            throw new IllegalArgumentException("Usuário já cadastrado");
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

        if (dados.statusSolicitacao() == null) {
            throw new IllegalArgumentException("Status da solicitação é obrigatório");
        }

        if (dados.enderecos() == null || dados.enderecos().isEmpty()) {
            throw new IllegalArgumentException("É obrigatório informar pelo menos um endereço");
        }

        if (!verificacao.pdfValido(documento)) {
            throw new IllegalArgumentException("Documento inválido (apenas PDF)");
        }

        Paciente paciente = new Paciente(
                dados.nome(),
                cpfNormalizado,
                dados.telefone(),
                dados.email(),
                dados.sexo(),
                dados.estadoCivil(),
                dados.statusSolicitacao(),
                dados.dataNascimento()
        );


        paciente = repositorioPaciente.save(paciente);

        for (EnderecoDTO dto : dados.enderecos()) {
            cadastrarEndereco(dto,paciente.getCpf());
        }

        if (documento != null){
            cadastrarDocumento(documento, paciente.getCpf());
        }

        return paciente;
    }


    public void cadastrarEndereco(EnderecoDTO enderecoDTO, String cpf){

        String cpfNormalizado = CpfUtils.normalizar(cpf);

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

        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        Endereco endereco = new Endereco(
                enderecoDTO.cep(),
                enderecoDTO.cidade(),
                enderecoDTO.estado(),
                enderecoDTO.bairro(),
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento()
        );

        endereco.setPaciente(paciente);
        paciente.adicionarEndereco(endereco);

        repositorioEndereco.save(endereco);
    }


    public void cadastrarDocumento(MultipartFile documento, String cpf){


        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }


        String cpfNormalizado = CpfUtils.normalizar(cpf);

        if (!verificacao.pdfValido(documento)) {
            throw new IllegalArgumentException("Documento inválido (somente PDF)");
        }

        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        String caminho = transformarDocumentacao
                .caminhoDocumentacao(documento, paciente.getCpf());

        Documentacao documentacao = new Documentacao();
        documentacao.setCaminho(caminho);
        documentacao.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
        documentacao.setDataEnvio(LocalDate.now());
        documentacao.setPaciente(paciente);

        paciente.adicionarDocumentacao(documentacao);
        repositorioDocumentacao.save(documentacao);
    }


}
