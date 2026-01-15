CREATE TABLE paciente(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL,
    status_solicitacao VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email VARCHAR (100) NOT NULL,
    sexo VARCHAR(20) NOT NULL,
    estado_civil VARCHAR(20) NOT NULL
);