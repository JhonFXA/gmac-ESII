CREATE TABLE medico(
    id_usuario UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    especializacao VARCHAR(50) NOT NULL,
    CONSTRAINT fk2_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
);