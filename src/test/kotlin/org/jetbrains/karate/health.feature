Feature: Healthcheck

  Background:
    * url baseUrl

  Scenario: health resource status is "up"
    Given url baseUrl + '/actuator/health'
    When method get
    Then status 200
    And match response == {'status':'UP'}
