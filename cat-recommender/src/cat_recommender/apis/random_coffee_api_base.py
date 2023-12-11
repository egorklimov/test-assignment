# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from cat_recommender.models.cat_for_coffee_response import CatForCoffeeResponse
from cat_recommender.models.suggest_cat_for_random_coffee_request import SuggestCatForRandomCoffeeRequest


class BaseRandomCoffeeApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseRandomCoffeeApi.subclasses = BaseRandomCoffeeApi.subclasses + (cls,)
    def suggest_cat(
        self,
        suggest_cat_for_random_coffee_request: SuggestCatForRandomCoffeeRequest,
    ) -> CatForCoffeeResponse:
        ...
