# coding: utf-8

from fastapi.testclient import TestClient


def test_suggest_cat(client: TestClient):
    suggest_cat_for_random_coffee_request = {
        "catId": 5,
        "name": "name",
        "breed": "breed"
    }

    response = client.post(
        "/api/recommend",
        json=suggest_cat_for_random_coffee_request,
    )
    assert response.status_code == 200
