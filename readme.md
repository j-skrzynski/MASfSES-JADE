## Typy zleceń

### LIMIT




# Pamiętać

## Agent giełdy
 giełda nasłuchuje cały czas na zlecenia aby je procesować i periodycznie zwraca informacje nt rozliczeń
 
# Polecenia
wysyłane przez brokera do giełdy

```
<traderName>#PLACE_ORDER;<orderName>;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;...

<traderName>#PLACE_ORDER;LIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>
<traderName>#PLACE_ORDER;NOLIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>
<traderName>#PLACE_ORDER;STOP;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<activationPrice>
<traderName>#PLACE_ORDER;STOPLIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>;<activationPrice>




orderName::= LIMIT, NOLIMIT, STOP, STOPLIMIT
orderType::= BUY,SELL
expirationSpecification::=  D
                            WDD/<numberOfSessions>
                            WDA
                            WDC/<numberOfSessions>/<numberOfMilliseconds>
```