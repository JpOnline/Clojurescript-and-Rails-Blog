![Bio Ritmo / Smart Fit](https://github.com/bioritmo/front-end-code-challenge/blob/master/biodevteam-2018.png)

# Backend code challenge

Este teste é apresentado às pessoas que estão se candidatando às vagas de desenvolvimento Backend.

#### Como enviar seu teste

* Faça um fork deste repositório.
* Abra um Pull Request.
* Na descrição do PR, coloque tudo que ache válido sobre o trabalho que fez, o que pensou em determinado momento ao tentar resolver o desafio, qualquer coisa que ache válido pra nós.

## O Desafio

Seu cliente é um blogueiro aficionado por tecnologia que tinha um blog muito visitado, porém, este blog foi hackeado e o ~~código fonte foi apagado~~.


Para o ressurgimento deste blog, este blogueiro teve a grande idéia de fazer todos os seus posts utilizando o formato [Markdown](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet).

Sendo assim ele precisa de um formulário onde ele pode preencher o título e o texto geral. Ele também deseja que a página inicial contenha a listagem das postagens e que seja possível visualizar essas postagens no formato final (já convertidos de markdown para HTML).

**O blogueiro aguarda ansiosamente pela primeira versão desse produto.**

### Desafio:
O blogueiro deve conseguir fazer posts em markdown.

#### Critério de aceitação:
_DADO QUE_ eu sou um blogueiro

_QUANDO_ eu acesso a página para criar uma postagem

_E_ crio uma postagem no formato markdown

_ENTÃO_ eu devo ter um link para o post final no formato html

#### O que é esperado:
- Implementar um background job (processador de trabalhos assincronos), para fazer a conversão de markdown para html.
- Uma página com a listagem das postagens
- Uma página para criação das postagens
- Uma página para visualização das postagens já convertidas
- Testes automatizados
- Utilizar Ruby on Rails
- Ferramenta de versionamento de código

###### Dicas:
Você pode utilizar qualquer biblioteca/gem que achar relevante para esse projeto, por exemplo: Se você não quer fazer uma conversão de markdown manualmente, pode utilizar a [RedCarpet](https://github.com/vmg/redcarpet) para fazer essa conversão por você.

Utilize sua criatividade para mostrar que suas habilidades vão além do básico:
- Implementar uma ferramenta de autenticação, assim os posts não ficam sem um dono
- Fazer testes de integração usando ferramentas especializadas
- Caprichar na estilização da página
- Nos surpreender com alguma implementação que melhore o fluxo para o usuário
