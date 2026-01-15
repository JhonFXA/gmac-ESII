CREATE TABLE endereco (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
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
