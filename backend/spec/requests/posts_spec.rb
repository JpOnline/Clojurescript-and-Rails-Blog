require 'rails_helper'

RSpec.describe 'Blog Posts API.', type: :request do
  # Test suite for POST /posts
  describe 'The POST /posts endpoint.' do
    let(:post_attributes) { { title: 'This is TDD',
                              content: '# Lorem Ipsum' } }

    context 'When /posts is called with blog post attributes' do
      before { post '/posts', params: post_attributes }

      it 'should create a blog post with the given title' do
        expect(json['post']['title']).to eq(post_attributes[:title])
      end

      it 'should create a blog post with the given content' do
        expect(json['post']['content']).to eq(post_attributes[:content])
      end

      it 'should provide user role of reader' do
        expect(json['user_role']).to eq('reader')
      end
    end

    context 'When post is submitted with a user logged as author' do
      before {
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}
        post '/posts', params: post_attributes,
                       headers: {"Authorization" => json['auth_token']}}

      it 'should create a blog post with the logged user as submited_by' do
        expect(json['post']['submited_by']).to eq('jpsoares106@gmail.com')
      end
    end

    context 'When GET /posts is called after submiting a post' do
      before { post '/posts', params: post_attributes
               get '/posts'}

      it 'should retrieve the submited post' do
        expect(json).not_to be_empty
      end

      it 'should retrieve the submited post with title' do
        expect(json.first['title']).to eq(post_attributes[:title])
      end

      it 'should retrieve the submited post with submited_by attribute' do
        expect(json.first['submited_by']).to eq(post_attributes[:submited_by])
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

    context 'When there is a post in DB and PUT /posts/1 is called changing the
    post' do
      before { post '/posts', params: post_attributes }

      it 'should retrieve a post with "Novo título" in its title' do
        put '/posts/1', params: {title: 'Novo título'}
        get '/posts'

        expect(json.first['title']).to eq('Novo título')
      end

      it 'should retrieve a post with "## Subtítulo" in its content.' do
        put '/posts/1', params: {content: '## Subtítulo'}
        get '/posts'

        expect(json.first['content']).to eq('## Subtítulo')
      end

      it 'should throw error when title is empty' do
        put '/posts/1', params: {title: ''}

        expect(response.body).to match(/Title can't be blank/)
      end
    end

  end

  # Test suite for DELETE /posts/:id
  describe 'DELETE /posts/:id' do
    let(:post_attributes) { { title: 'Título do Post',
                              submited_by: 'jpsoares106@gmail.com',
                              content: '# Lorem Ipsum' } }

    context 'When there is a post in DB and DELETE /posts/1 is called' do
      before { post '/posts', params: post_attributes }

      it 'should retrieve no posts' do
        delete '/posts/1'
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
