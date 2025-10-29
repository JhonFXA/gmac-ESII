CREATE TABLE medico(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    especializacao VARCHAR(50) NOT NULL,
    CONSTRAINT fk_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
);