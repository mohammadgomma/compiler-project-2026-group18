from flask import Flask, render_template, request

app = Flask(__name__)

@app.route('/add_product', methods=['GET', 'POST'])
def add_product():
    if request.method == 'POST':
        name = request.form.get('name')
        price = request.form.get('price')
        description = request.form.get('description')
        return render_template('success.html', name=name, price=price)
    return render_template('add_product.html')

if __name__ == '__main__':
    app.run()
