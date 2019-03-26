class PostsController < ApplicationController

  # GET /posts
  def index
    @posts = Post.all
    json_response(@posts)
  end

  # POST /posts
  def create
    validate_author 'Only authors can create posts.'

    @post = Post.new(post_params)
    @post[:submited_by] = current_user[:email] if current_user
    @post.save!

    json_response(@post, :created)
  end

  # PUT /posts/:id
  def update
    validate_author 'Only authors can edit posts.'

    @post = Post.find(params[:id])
    @post.update!(post_params)
    head :no_content
  end

  # DELETE /posts/:id
  def destroy
    validate_author 'Only authors can delete posts.'

    @post = Post.find(params[:id])
    @post.destroy
    head :no_content
  end


  private

  def post_params
    params.permit(:title, :submited_by, :content)
  end

  def validate_author(error_message)
    if user_role != 'author'
      raise(ExceptionHandler::UnauthorizedRequest, error_message)
    end
  end
end
