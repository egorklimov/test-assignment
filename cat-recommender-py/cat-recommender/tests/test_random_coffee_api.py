# coding: utf-8

from fastapi.testclient import TestClient


from cat_recommender.models.cat_for_coffee_response import CatForCoffeeResponse  # noqa: F401
from cat_recommender.models.suggest_cat_for_random_coffee_request import SuggestCatForRandomCoffeeRequest  # noqa: F401


def test_suggest_cat(client: TestClient):
    """Test case for suggest_cat

    
    """
    suggest_cat_for_random_coffee_request = {"cat_id":0,"name":"name","breed":"breed"}

    headers = {
    }
    response = client.request(
        "POST",
        "/api/recommend",
        headers=headers,
        json=suggest_cat_for_random_coffee_request,
    )

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200

