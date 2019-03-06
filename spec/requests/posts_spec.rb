require 'rails_helper'

RSpec.describe 'Blog Posts API.', type: :request do
  # Test suite for POST /posts
  describe 'The POST /posts endpoint.' do
    let(:post_attributes) { { title: 'This is TDD',
                              created_by: 'jpsoares106@gmail.com',
                              content: '# Lorem Ipsum' } }

    context 'When /posts is called with blog post attributes' do
      before { post '/posts', params: post_attributes }

      it 'should create a blog post' do
        expect(json['title']).to eq(post_attributes[:title])
      end
    end
  end

  private
  def json
    JSON.parse(response.body)
  end
end
