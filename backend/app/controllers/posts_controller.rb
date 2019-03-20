class PostsController < ApplicationController

  # GET /posts
  def index
    @posts = Post.all
    json_response(@posts)
  end

  # POST /posts
  def create
    if user_role != 'author'
      return json_response({message: 'Apenas autores podem criar posts.'},
                           :unauthorized)
    end

    @post = Post.new(post_params)
    @post[:submited_by] = current_user[:email] if current_user
    @post.save!

    json_response(@post, :created)
  end

  # PUT /posts/:id
  def update
    if user_role != 'author'
      return json_response({message: 'Apenas autores podem editar posts.'},
                           :unauthorized)
    end

    @post = Post.find(params[:id])
    @post.update!(post_params)
    head :no_content
  end

  # DELETE /posts/:id
  def destroy
    if user_role != 'author'
      return json_response({message: 'Apenas autores podem deletar posts.'},
                           :unauthorized)
    end

    @post = Post.find(params[:id])
    @post.destroy
    head :no_content
  end


  private

  def post_params
    params.permit(:title, :submited_by, :content)
  end
end
