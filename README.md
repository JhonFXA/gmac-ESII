# 📌 Endpoints da API

Este documento descreve os endpoints disponíveis na API, organizados por módulo, incluindo método HTTP, rota e perfis de acesso autorizados conforme configuração de segurança (Spring Security).

---

## 🔐 Autenticação

| Método | Endpoint      | Acesso  |
| ------ | ------------- | ------- |
| POST   | `/auth/login` | Público |

---

## 👤 Usuário

| Método | Endpoint                | Acesso        |
| ------ | ----------------------- | ------------- |
| GET    | `/usuario/buscar/{cpf}` | ADMINISTRADOR |
| POST   | `/usuario/registro`     | ADMINISTRADOR |
| PUT    | `/usuario/alterar`      | ADMINISTRADOR |
| GET    | `/usuario/listar`       | ADMINISTRADOR |

---

## 🧑‍⚕️ Paciente

| Método | Endpoint                    | Acesso                               |
| ------ | --------------------------- | ------------------------------------ |
| POST   | `/paciente/cadastrar`       | ADMINISTRADOR, RECEPCIONISTA         |
| POST   | `/paciente/{cpf}/endereco`  | ADMINISTRADOR, RECEPCIONISTA         |
| POST   | `/paciente/{cpf}/documento` | ADMINISTRADOR, RECEPCIONISTA         |
| PUT    | `/paciente/alterar`         | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| GET    | `/paciente/buscar/{cpf}`    | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| GET    | `/paciente/listar`          | ADMINISTRADOR, RECEPCIONISTA, MEDICO |

---

## 📄 Documentação

| Método | Endpoint                              | Acesso                               |
| ------ | ------------------------------------- | ------------------------------------ |
| GET    | `/documentacao/buscar`                | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| GET    | `/documentacao/buscar/{id}`           | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| GET    | `/documentacao/buscar/validacao/{id}` | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| GET    | `/documentacao/url/{id}`              | ADMINISTRADOR, MEDICO                |
| POST   | `/documentacao/validar`               | ADMINISTRADOR, RECEPCIONISTA, MEDICO |

---

## 🩺 Perícia

| Método | Endpoint                  | Acesso                               |
| ------ | ------------------------- | ------------------------------------ |
| POST   | `/pericia/marcar`         | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| PUT    | `/pericia/validarPericia` | MEDICO                               |
| POST   | `/pericia/listar`         | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| PUT    | `/pericia/{id}/cancelar`  | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| PUT    | `/pericia/{id}/remarcar`  | ADMINISTRADOR, RECEPCIONISTA, MEDICO |

---

## 📊 Relatórios

| Método | Endpoint               | Acesso  |
| ------ | ---------------------- | ------- |
| GET    | `/relatorio/dashboard` | Público |

---

## ⚙️ Observações Gerais

* Todas as requisições `OPTIONS /**` são liberadas (CORS).
* Qualquer endpoint não listado explicitamente exige autenticação.
* Os perfis utilizam o padrão `ROLE_` internamente (ex: `ROLE_ADMINISTRADOR`).

---

📘 **Sugestão**: este README pode ser colocado na raiz do projeto ou integrado a uma documentação Swagger/OpenAPI para visualização interativa.
