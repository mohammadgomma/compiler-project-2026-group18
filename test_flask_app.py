import os
import sys

import pytest

# Import the generated application—the integration test intentionally validates
# compiler output rather than the source fixture.
GENERATED_APP_DIR = os.path.abspath('generated/crud_flask_app')
if GENERATED_APP_DIR not in sys.path:
    sys.path.insert(0, GENERATED_APP_DIR)

from app import app, products, counters

INITIAL_PRODUCTS = {
    1: {"id": 1, "name": "Laptop", "price": 1200, "description": "High-end gaming laptop"},
    2: {"id": 2, "name": "Smartphone", "price": 800, "description": "Latest model smartphone"},
}


@pytest.fixture
def client():
    products.clear()
    products.update({key: value.copy() for key, value in INITIAL_PRODUCTS.items()})
    counters["next_id"] = 3
    app.config['TESTING'] = True
    with app.test_client() as test_client:
        yield test_client


def test_crud_lifecycle(client):
    response = client.get('/products')
    assert response.status_code == 200
    assert b'Laptop' in response.data
    assert b'Smartphone' in response.data

    response = client.post('/products/add', data={
        'name': 'Tablet',
        'price': '300',
        'description': 'A new tablet',
    }, follow_redirects=True)
    assert response.status_code == 200
    assert b'Tablet' in response.data

    response = client.get('/products/3')
    assert response.status_code == 200
    assert b'A new tablet' in response.data

    response = client.post('/products/1/delete', follow_redirects=True)
    assert response.status_code == 200
    assert b'Laptop' not in response.data
    assert b'Tablet' in response.data

    response = client.get('/products/2')
    assert response.status_code == 200
    assert b'Smartphone' in response.data

    response = client.post('/products/add', data={
        'name': 'Smartwatch',
        'price': '200',
        'description': 'A cool watch',
    }, follow_redirects=True)
    assert response.status_code == 200
    assert 4 in products
    assert products[4]['name'] == 'Smartwatch'
    assert len(products) == len(set(products.keys()))

    response = client.get('/products')
    assert response.status_code == 200
    assert b'Smartwatch' in response.data
    assert b'/products/4' in response.data

    response = client.get('/products/999')
    assert response.status_code == 404

    response = client.post('/products/4/delete', follow_redirects=True)
    assert response.status_code == 200
    assert b'Smartwatch' not in response.data
    assert 4 not in products


def test_missing_required_form_data_returns_400_without_consuming_id(client):
    next_id_before = counters['next_id']
    response = client.post('/products/add', data={'description': 'missing name and price'})
    assert response.status_code == 400
    assert counters['next_id'] == next_id_before
    assert next_id_before not in products
