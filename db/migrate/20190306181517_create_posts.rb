class CreatePosts < ActiveRecord::Migration[5.2]
  def change
    create_table :posts do |t|
      t.string :title
      t.string :submited_by
      t.text :content

      t.timestamps
    end
  end
end
