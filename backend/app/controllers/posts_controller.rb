class PostsController < ApplicationController

  # GET /posts
  def index
    @posts = Post.all
    json_response(@posts)
  end

  # POST /posts
  def create
    @post = Post.new(post_params)
    @post[:submited_by] = current_user[:email] if current_user
    @post.save!

    json_response({post: @post,
                   user_role: user_role}, :created)
  end

  # PUT /posts/:id
  def update
    @post = Post.find(params[:id])
    @post.update!(post_params)
    head :no_content
  end

  # DELETE /posts/:id
  def destroy
    @post = Post.find(params[:id])
    @post.destroy
    head :no_content
  end


  private

  def post_params
    params.permit(:title, :submited_by, :content)
  end
end
