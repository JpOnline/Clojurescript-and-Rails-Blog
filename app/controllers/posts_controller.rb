class PostsController < ApplicationController

  # GET /posts
  def index
    @posts = Post.all
    json_response(@posts)
  end

  # POST /posts
  def create
    @post = Post.create!(params.permit(:title, :submited_by, :content))
    json_response(@post, :created)
  end

  def json_response(object, status = :ok)
    render json: object, status: status
  end
end
