require 'rails_helper'

RSpec.describe 'Blog Posts API.', type: :request do
  # Test suite for POST /posts
  describe 'The POST /posts endpoint.' do
    let(:post_attributes) { { title: 'This is TDD',
                              submited_by: 'jpsoares106@gmail.com',
                              content: '# Lorem Ipsum' } }

    context 'When /posts is called with blog post attributes' do
      before { post '/posts', params: post_attributes }

      it 'should create a blog post with the given title' do
        expect(json['title']).to eq(post_attributes[:title])
      end

      it 'should create a blog post with the given created_by' do
        expect(json['submited_by']).to eq(post_attributes[:submited_by])
      end

      it 'should create a blog post with the given content' do
        expect(json['content']).to eq(post_attributes[:content])
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
    content of the post' do
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
    end

  end

  private
  def json
    JSON.parse(response.body)
  end
end
