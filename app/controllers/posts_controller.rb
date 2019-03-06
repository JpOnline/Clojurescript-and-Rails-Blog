class PostsController < ApplicationController

  # POST /posts
  def create
    @post = Post.create!(params.permit(:title, :submited_by, :content))
    render json: @post, status: :created
  end
end
