CREATE TABLE administrador(
    id_usuario UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    CONSTRAINT fk4_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
);