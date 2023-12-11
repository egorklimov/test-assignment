# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil
import random

from cat_recommender.apis.random_coffee_api_base import BaseRandomCoffeeApi
import cat_recommender

from fastapi import (  # noqa: F401
    APIRouter,
    Body,
    Cookie,
    Depends,
    Form,
    Header,
    HTTPException,
    Path,
    Query,
    Response,
    Security,
    status,
)

from cat_recommender.models.extra_models import TokenModel  # noqa: F401
from cat_recommender.models.cat_for_coffee_response import CatForCoffeeResponse
from cat_recommender.models.suggest_cat_for_random_coffee_request import SuggestCatForRandomCoffeeRequest


router = APIRouter()

ns_pkg = cat_recommender
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/api/recommend",
    responses={
        200: {"model": CatForCoffeeResponse, "description": "Suggested cat."},
    },
    tags=["RandomCoffee"],
    response_model_by_alias=True,
)
async def suggest_cat(
    suggest_cat_for_random_coffee_request: SuggestCatForRandomCoffeeRequest = Body(None, description="Suggests a cat for a coffee."),
) -> CatForCoffeeResponse:
    cat_id = suggest_cat_for_random_coffee_request.cat_id
    if cat_id % 1001 == 0:
        raise HTTPException(status_code=500, detail="Oops, bad number")

    return CatForCoffeeResponse(id=random.randint(1, cat_id - 1))
