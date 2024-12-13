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