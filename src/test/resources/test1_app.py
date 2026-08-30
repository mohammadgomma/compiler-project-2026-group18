from flask import Flask, render_template

app = Flask(__name__)

products = [
    {"id": 1, "name": "Laptop", "price": 999.99, "image": "laptop.jpg"},
    {"id": 2, "name": "Phone", "price": 699.99, "image": "phone.jpg"},
    {"id": 3, "name": "Tablet", "price": 499.99, "image": "tablet.jpg"}
]

@app.route('/products')
def display_products():
    return render_template('products.html', products=products)

if __name__ == '__main__':
    app.run()
