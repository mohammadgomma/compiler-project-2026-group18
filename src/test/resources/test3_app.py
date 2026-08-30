from flask import Flask, render_template

app = Flask(__name__)

@app.route('/product/<int:product_id>')
def product_details(product_id):
    product = {
        "id": product_id,
        "name": "Laptop Pro",
        "price": 1299.99,
        "description": "High-performance laptop with 16GB RAM and 512GB SSD",
        "image": "laptop_pro.jpg",
        "specs": ["Intel Core i7", "16GB RAM", "512GB SSD", "15.6 inch Display"]
    }
    return render_template('product_details.html', product=product)

if __name__ == '__main__':
    app.run()
