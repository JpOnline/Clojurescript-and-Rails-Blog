class PostsController < ApplicationController

  # GET /posts
  def index
    @posts = Post.all
    json_response(@posts)
  end

  # POST /posts
  def create
    @post = Post.create!(post_params)
    json_response(@post, :created)
  end

  # PUT /posts/:id
  def update
    @post = Post.find(params[:id])
    @post.update(post_params)
    head :no_content
  end

  # DELETE /posts/:id
  def destroy
    @post = Post.find(params[:id])
    @post.destroy
    head :no_content
  end


  private

  def json_response(object, status = :ok)
    render json: object, status: status
  end

  def post_params
    params.permit(:title, :submited_by, :content)
  end
end
