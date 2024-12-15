## Typy zleceń

### LIMIT




# Pamiętać

## Agent giełdy
 giełda nasłuchuje cały czas na zlecenia aby je procesować i periodycznie zwraca informacje nt rozliczeń
 
# Polecenia
wysyłane przez brokera do giełdy

```
<traderName>/<BrokerOrderId>#PLACE_ORDER;<orderName>;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;...

<traderName>/<BrokerOrderId>#PLACE_ORDER;LIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>
<traderName>/<BrokerOrderId>#PLACE_ORDER;NOLIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>
<traderName>/<BrokerOrderId>#PLACE_ORDER;STOP;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<activationPrice>
<traderName>/<BrokerOrderId>#PLACE_ORDER;STOPLIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>;<activationPrice>




orderName::= LIMIT, NOLIMIT, STOP, STOPLIMIT
orderType::= BUY,SELL
expirationSpecification::=  D
                            WDD/<numberOfSessions>
                            WDA
                            WDC/<numberOfSessions>/<numberOfMilliseconds>



#ADD_STOCK;TestStock;XD;20;1000
#ADD_STOCK;nazwa dluga;skrot;ipo price;ilosc udzialow
```

Trader1/1#PLACE_ORDER;LIMIT;SELL;WDD/5;XD;100;102
Trader2/1#PLACE_ORDER;NOLIMIT;BUY;WDD/5;XD;100



Trader1/2#PLACE_ORDER;LIMIT;SELL;WDA;XD;100;102
Trader2/2#PLACE_ORDER;LIMIT;SELL;WDA;XD;100;103
Trader3/1#PLACE_ORDER;NOLIMIT;SELL;WDA;XD;100
Trader4/1#PLACE_ORDER;LIMIT;BUY;WDA;XD;300;102


---

```

{
  "command": "ADD_STOCK",
  "arguments": ["Apple", "AAPL", 150.0, 1000000],
  "traderName": "",
  "brokerName": "",
  "exchangeName": "",
  "brokerOrderId": ""
}


{
  "command": "PLACE_ORDER",
  "arguments": [{
    "order": {
      "symbol": {"shortName": "AAPL"},
      "orderType": "BUY",
      "price": 150.0,
      "quantity": 1000,
      "expirationSecpification": "D"
    },
    "awaiting": false,
    "price": 0.0
  }],
  "traderName": "JohnDoe",
  "brokerName": "XYZBroker",
  "exchangeName": "GPW",
  "brokerOrderId": ""
}


{
  "command": "PLACE_ORDER",
  "arguments": [{
    "order": {
      "symbol": {"shortName": "AAPL"},
      "orderType": "SELL",
      "price": 149.0,
      "quantity": 1000,
      "expirationSecpification": "D"
    },
    "awaiting": false,
    "price": 150.0
  }],
  "traderName": "sMITH",
  "brokerName": "XYZBroker",
  "exchangeName": "GPW",
  "brokerOrderId": ""
}


{
  "command": "PLACE_ORDER",
  "arguments": [{
    "order": {
      "symbol": {"shortName": "AAPL"},
      "orderType": "SELL",
      "price": null,
      "quantity": 500,
      "expirationSecpification": "D"
    },
    "awaiting": true,
    "price": 145.0
  }],
  "traderName": "JohnDoe",
  "brokerName": "XYZBroker",
  "exchangeName": "NYSE",
  "brokerOrderId": ""
}

{
  "command": "PLACE_ORDER",
  "arguments": [{
    "order": {
      "symbol": {"shortName": "AAPL"},
      "orderType": "SELL",
      "price": null,
      "quantity": 500,
      "expirationSecpification": "D"
    },
    "awaiting": true,
    "price": 145.0
  }],
  "traderName": "JohnDoe",
  "brokerName": "XYZBroker",
  "exchangeName": "NYSE",
  "brokerOrderId": ""
}


{
  "command": "PLACE_ORDER",
  "arguments": [{
    "order": {
      "symbol": {"shortName": "AAPL"},
      "orderType": "SELL",
      "price": 145.0,
      "quantity": 500,
      "expirationSecpification": "D"
    },
    "awaiting": true,
    "price": 140.0
  }],
  "traderName": "JohnDoe",
  "brokerName": "XYZBroker",
  "exchangeName": "NYSE",
  "brokerOrderId": ""
}

{
  "command": "GET_TOP_BUY",
  "arguments": ["AAPL"],
  "traderName": "JohnDoe",
  "brokerName": "XYZBroker",
  "exchangeName": "NYSE",
  "brokerOrderId": ""
}


```