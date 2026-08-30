from flask import Flask, render_template, request, redirect, url_for, abort

app = Flask(__name__)

# Using a dictionary for products and a separate counter for ID management
# This prevents IndexError and duplicate IDs on deletion.
counters = {"next_id": 3}
products = {
    1: {"id": 1, "name": "Laptop", "price": 1200, "description": "High-end gaming laptop"},
    2: {"id": 2, "name": "Smartphone", "price": 800, "description": "Latest model smartphone"}
}

@app.route('/products')
def list_products():
    return render_template('products.html', products=products.values())

@app.route('/products/add', methods=['GET', 'POST'])
def add_product():
    if request.method == 'POST':
        name = request.form.get('name')
        price = request.form.get('price')
        desc = request.form.get('description', '')
        if not name or not price:
            return abort(400)

        new_id = counters["next_id"]
        counters["next_id"] = new_id + 1

        new_prod = {"id": new_id, "name": name, "price": price, "description": desc}
        products[new_id] = new_prod
        
        return redirect(url_for('list_products'))
    else:
        return render_template('add_product.html')

@app.route('/products/<int:id>')
def product_detail(id):
    if id in products:
        product = products[id]
        return render_template('product_detail.html', product=product)
    else:
        return abort(404)

@app.route('/products/<int:id>/delete', methods=['POST'])
def delete_product(id):
    if id in products:
        products.pop(id)
    return redirect(url_for('list_products'))

if __name__ == '__main__':
    app.run(debug=True)
