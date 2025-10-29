CREATE TABLE endereco(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    numero VARCHAR (10) NOT NULL,
    logradouro VARCHAR(50) NOT NULL,
    complemento VARCHAR(50)
);