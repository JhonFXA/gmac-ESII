CREATE TABLE recepcionista(
    id_usuario UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    CONSTRAINT fk3_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
);