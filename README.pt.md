[<img alt="English version" height="21" width="21" src="http://aux4.iconspalace.com/uploads/76464348934985440.png">
](README.md)

# Blog em Clojurescript e Rails

Esse projeto é parte do processo de contratação da Bio Ritmo. Ele foi projeto com
um design *mobile first*, consiste em um servidor (backend) desenvolvido em Ruby
on Rails que expõe uma API e um cliente (frontend) que consome essa API no estilo
Single Page Application (SPA) utilizando Clojurescript on
[Re-frame](https://github.com/Day8/re-frame).

Você pode ver o app em https://jponline.github.io/Clojurescript-and-Rails-Blog e seus devcards em https://jponline.github.io/Clojurescript-and-Rails-Blog/cards

## Rodando o App

Para rodar o servidor é preciso ter o [Rails instalado](https://guides.rubyonrails.org/getting_started.html#installing-rails)
e entrar com os comandos

```
/backend$ bundle install
/backend$ rails s
```

no diretório `backend`. Então o servidor começará a rodar em
http://localhost:3000/

Pra rodar o frontend é preciso ter instalado [Leiningen](https://leiningen.org/) e
entrar com o comando

`/frontend$ lein figwheel dev devcards`

no diretório `frontend`. Então o app poderá ser usado em http://localhost:3449/

Obs: Você pode usar `ctrl-h` para fechar o painel de debug do Re-frame.

## Autenticação

Para criar, editar e excluir posts é preciso estar **logado**. O app possui um único
**autor**, apenas um usuário é capaz de executar as ações de autor, porém para melhor
demonstração abilitei o papel de autor para qualquer usuário logado.

A autenticação é feita por **verificação de email**. Um código veriicador é mandado ao
seu email ao informá-lo na tela de login. O código também é exibido no console do
servidor caso não queira verificar seu email.

## Editando posts

Cada alteração no post é mandado ao servidor automaticamente (um *spinner* indica
sempre que se espera uma resposta do servidor). A transformação de markdown para
HTML pode ser vista em tempo real enquanto se digita o texto.

## Testes

No backend optei por utilizar o framework de teste [RSpec](http://rspec.info/) ao
invés do padrão *Minitest*. Os casos de teste estão definidos em dois arquivos:
`backend/spec/requests/authentication_spec.rb` e `backend/spec/requests/posts_spec.rb`.
Para rodá-los entre com o comando

`/backend$ bundle exec rspec`

No frontend optei por utilizar [Devcards](https://github.com/bhauman/devcards),
ele permite isolar **views** e **componentes** para serem testados e prototipados
fora do contexto do aplicativo. Também desenvi testes usando a estratégia *Humble
View*, isolando o modelo das *views* verificando que os **eventos** alteram o
modelo como esperado. Esses testes podem ser verificados em

- http://localhost:3449/cards.html#!/frontend.views_prototypes e;
- http://localhost:3449/cards.html#!/frontend.events_tests
