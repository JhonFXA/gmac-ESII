CREATE TABLE IF NOT EXISTS paciente (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL,
    status_solicitacao VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    sexo VARCHAR(20) NOT NULL,
    estado_civil VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS endereco (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    logradouro VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(50),

    paciente_id UUID NOT NULL,

    CONSTRAINT fk_endereco_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS documentacao (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    caminho VARCHAR(100) NOT NULL,

    paciente_id UUID NOT NULL,

    CONSTRAINT fk_documentacao_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
        ON DELETE CASCADE
);
