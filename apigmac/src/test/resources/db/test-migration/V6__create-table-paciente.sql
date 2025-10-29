CREATE TABLE paciente(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL,
    status_solicitacao VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email VARCHAR (100) NOT NULL,
    sexo VARCHAR(20) NOT NULL,
    estado_civil VARCHAR(20) NOT NULL,
    url_documentacao VARCHAR(100) NOT NULL,
    id_endereco UUID NOT NULL,
    CONSTRAINT fk_endereco
                FOREIGN KEY (id_endereco)
                REFERENCES endereco(id)
);