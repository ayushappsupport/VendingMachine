# Vending Machine API

I have implemented a REST API for Vending Machines Change Component using Java Spring. I used a REST API with JSON as this is an recognised standard of API. The API can be tested in a normal web browser,
POST's may require you to download a web browser plug in for example POSTMAN. 



The following actions can be performed


```
localhost:8080/machine/{machineId}/coins - GET: View all the coins that vending machine currently has stored
```
```
localhost:8080/machine/{machineId}/coins/addInitialCoins - POST: Initialize the vending machine with the coins
```
```
localhost:8080/machine/{machineId}/coins/addCoins - POST: Add a Single Coin in the Vending Machine
```

```
localhost:8080/machine/{machineId}/coins/refund - POST: Returns you coins of equal value to the amount that was put into the machine (Uses up larger coins first)
```

Swagger Documentation: http://localhost:8080/swagger-ui.html

Health Check : http://localhost:8080/actuator/health

All the Details to run the API either through postman or interactive test suite  are mentioned in the document "Vending_Machine_RunThrough_Document".
Please refer to the document.

Notes:

When testing POST ensure the header "Content-Type" is set to "application/json

Initially there is only 1 Vending Machine which we are giving in the Request of every API.