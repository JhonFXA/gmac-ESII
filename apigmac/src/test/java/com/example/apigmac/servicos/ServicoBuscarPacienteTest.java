package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Endereco;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoBuscarPaciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoBuscarPacienteTest {

    @Mock
    private RepositorioPaciente repositorioPaciente;

    @InjectMocks
    private ServicoBuscarPaciente servico;

    private Paciente pacienteMock;
    private final String CPF_TESTE = "123.456.789-01";
    private final String CPF_NORMALIZADO = "12345678901";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Instanciando um paciente real para o Mock retornar
        pacienteMock = new Paciente();
        pacienteMock.setNome("João Silva");
        pacienteMock.setCpf(CPF_TESTE);
        pacienteMock.setStatusSolicitacao(StatusSolicitacao.PENDENTE);
        pacienteMock.setDataNascimento(LocalDate.of(1995, 5, 20));
        pacienteMock.setTelefone("79999999999");
        pacienteMock.setEmail("joao@email.com");
        pacienteMock.setSexo(Sexo.MASCULINO);
        pacienteMock.setEstadoCivil(EstadoCivil.SOLTEIRO);

        // Criando uma lista de endereços para testar o mapeamento do Stream
        Endereco endereco = new Endereco("49000000", "Aracaju", "SE", "Centro", "Rua A", "10", "Apto 1");
        pacienteMock.setEnderecos(new ArrayList<>());
        pacienteMock.getEnderecos().add(endereco);
    }
    @Test
    void deveRetornarPacienteDtoQuandoCpfExistir() {

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO))
                .thenReturn(pacienteMock);

        PacienteDTO resultado = servico.buscarPaciente(CPF_TESTE);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nome());
        assertEquals("123.456.789-01", resultado.cpf()); // porque o serviço formata
        assertEquals(1, resultado.enderecos().size());
    }


    @Test
    void deveLancarExcecaoQuandoPacienteNaoForEncontrado() {
        // Arrange
        when(repositorioPaciente.findByCpf("000.000.000-00")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servico.buscarPaciente("000.000.000-00");
        });

        assertEquals("Paciente não encontrado", exception.getMessage());
        verify(repositorioPaciente, times(1)).findByCpf(anyString());
    }
}