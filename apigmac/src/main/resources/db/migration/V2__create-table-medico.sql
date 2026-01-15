CREATE TABLE medico(
    id_usuario UUID PRIMARY KEY,
    especializacao VARCHAR(50) NOT NULL,
    CONSTRAINT fk_medico_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
            ON DELETE CASCADE
);