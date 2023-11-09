Feature: Task API

  Background:
    * url baseUrl
    * def baseExampleAPIPath =  baseUrl + '/api/cats'

  Scenario: example post request
    Given url baseExampleAPIPath
    When method get
    Then status 200
