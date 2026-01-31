# Emergy API

## Sobre o Projeto
A **Emergy API** é o core backend de um sistema voltado para o cálculo e gestão de simulações de **emergia**.  
Este projeto representa a evolução de uma ferramenta legada desenvolvida originalmente em **JavaFX**, agora refatorada para uma arquitetura moderna, utilizando **Spring Boot**.
O objetivo principal da migração foi **desacoplar a lógica de negócio da interface desktop**, permitindo que o sistema seja consumido por uma interface **RESTful**.

---

## Tecnologias Utilizadas
- **Linguagem:** Java 17 (utilizando *Records* para DTOs e imutabilidade)
- **Framework:** Spring Boot 3.x
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:**  
  - H2 (Testes / Desenvolvimento)  
  - PostgreSQL (Produção)
- **Testes:** JUnit 5, Mockito, MockMvc
- **Segurança & Validação:** Bean Validation e Exception Handling customizado

---

## Arquitetura e Padrões
O projeto segue o padrão de camadas recomendado pelo ecossistema Spring, garantindo **baixo acoplamento** e **alta coesão**:

- **Controllers:** Exposição dos endpoints REST e manipulação de requisições HTTP
- **Services:** Camada de lógica de negócio, onde residem as regras de validação e processamento
- **Repositories:** Comunicação com o banco de dados via Spring Data JPA
- **Entities / DTOs:** Modelagem de dados e objetos de transferência, evitando a exposição direta das entidades

---

## 🧪 Qualidade de Código e Testes
Um dos pilares desta migração foi a garantia de estabilidade.

- **Testes de Integração de Repositório:**  
  Validação do mapeamento JPA e queries customizadas utilizando `@DataJpaTest`
- **Testes Unitários de Serviço:**  
  Cobertura da lógica de negócio com Mockito para isolamento de dependências
- **Testes da Camada Web:**  
  Validação dos controllers e contratos JSON utilizando MockMvc

---
