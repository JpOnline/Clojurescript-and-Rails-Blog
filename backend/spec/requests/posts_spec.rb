require 'rails_helper'

RSpec.describe 'Blog Posts API.', type: :request do
  # Test suite for POST /posts
  describe 'The POST /posts endpoint.' do
    let(:post_attributes) { { title: 'This is TDD',
                              content: '# Lorem Ipsum' } }

    context 'Given user is not logged.
             When /posts is called with blog post attributes' do
      before { post '/posts', params: post_attributes }

      it 'should send an error message saying only authors can create posts' do
        expect(response.body).to match(/Apenas autores podem criar posts/)
      end
    end

    context 'Given user is logged.
             When /posts is called with blog post attributes' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => json['auth_token']}}

      it 'should create a blog post with the given title' do
        expect(json['title']).to eq(post_attributes[:title])
      end

      it 'should create a blog post with the logged user as submited_by' do
        expect(json['submited_by']).to eq('jpsoares106@gmail.com')
      end

      it 'should create a blog post with the given content' do
        expect(json['content']).to eq(post_attributes[:content])
      end
    end

    context 'When the wrong JWT is given.' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => "Wront JWT"}}

      it 'should send an error message saying that is an invalid JWT' do
        expect(response.body).to match(/Inválido Json Web Token/)
      end
    end

    context 'When GET /posts is called after submiting a post' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => json['auth_token']}
        get '/posts'}

      it 'should retrieve the submited post' do
        expect(json).not_to be_empty
      end

      it 'should retrieve the submited post with title' do
        expect(json.first['title']).to eq(post_attributes[:title])
      end

      it 'should retrieve the submited post with submited_by attribute' do
        expect(json.first['submited_by']).to eq('jpsoares106@gmail.com')
      end

      it 'should retrieve the submited post with content' do
        expect(json.first['content']).to eq(post_attributes[:content])
      end
    end
  end

  # Test suite for PUT /posts/:id
  describe 'PUT /posts/:id' do
    let(:post_attributes) { { title: 'Título do Post',
                              submited_by: 'jpsoares106@gmail.com',
                              content: '# Lorem Ipsum' } }

    context 'Given there is a post in DB and user is not author.' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => json['auth_token']}}

      it 'When PUT /posts/1 is called changing title,
          it should send an error message saying only authors can update posts' do
        put '/posts/1', params: {title: 'Novo título'}

        expect(response.body).to match(/Apenas autores podem editar posts/)
      end
    end

    context 'Given there is a post in DB and user is an author.' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        @auth_token = json['auth_token']
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => @auth_token}}

      it 'When PUT /posts/1 is called changing the post,
          it should retrieve a post with "Novo título" in its title' do
        put '/posts/1', params: {title: 'Novo título'},
                        headers: {"Authorization" => @auth_token}
        get '/posts'

        expect(json.first['title']).to eq('Novo título')
      end

      it 'should retrieve a post with "## Subtítulo" in its content.' do
        put '/posts/1', params: {content: '## Subtítulo'},
                        headers: {"Authorization" => @auth_token}
        get '/posts'

        expect(json.first['content']).to eq('## Subtítulo')
      end

      it 'should throw error when title is empty' do
        put '/posts/1', params: {title: ''},
                        headers: {"Authorization" => @auth_token}

        expect(response.body).to match(/Title can't be blank/)
      end
    end

  end

  # Test suite for DELETE /posts/:id
  describe 'DELETE /posts/:id' do
    let(:post_attributes) { { title: 'Título do Post',
                              content: '# Lorem Ipsum' } }

    context 'Given there is a post in DB' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        @auth_token = json['auth_token']
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => @auth_token}}

      it 'When a not logged user calls DELETE /posts/1' do
        delete '/posts/1'

        expect(response.body).to match(/Apenas autores podem deletar posts/)
      end

      it 'When an author user calls DELETE /posts/1' do
        delete '/posts/1', headers: {"Authorization" => @auth_token}
        get '/posts'

        expect(json).to be_empty
      end
    end
  end


  private

  def json
    JSON.parse(response.body)
  end
end
