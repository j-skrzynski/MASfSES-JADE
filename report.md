# Source of data

Data was gathered from https://finance.yahoo.com/quote/AAPL/ as day aggregates.

In order to perform simple simulations daily aggregates should be good enough. Access to minute aggregates that give a lot more information is very restricted and limited only to several days. 

Code below is used to extract the meaningfull part of the data to prepare the simulation
```python
import pandas as pd

df = pd.read_csv("/content/AAPL (20250102000000000 _ 20230310000000000).csv")

repr(list(df["Close"]))
```

Data is used as impulse to cause the change of price towards the real life scenario in each session. Such impulse causes price to oscilate around the real life values. In order to manipulate strength of the impulse one needs to adjust number of shares ofered at that price. To make the simulation stable and convergent towards the historic prices, one needs to adjust the number of shares offered at historic prices to be at least comparable with volume of shares traded by agents. If number would be to small it will not cause emergence of price recognition, in case of to big value it may cause the market to directly follow the historic data maiking it impossible to observe the realistic behaviour of agents.

This way of supplying data into system provides a signifficant advantage of causing realistic conditions without a need to simulate big system. The imaginary trader who sells stock at historic prices embraces the whole behaviour of the real stock exchange traders eliminating the need of simulating it. The imaginary trader plays a role of a huge player that is able to dictate a price, that is an abstraction encapsulating a whole market in perception of a singular trader. As mentioned above this imaginary entity has some power like the market subjected to the investor, however even though market is superior to investor, investor still my weaken impact of this big entity actions, by correct strategy. Simulating multiple agents, causing temporal spike may result in emergence - other agents will see the change in trend and cause the price to oscilate.

Description above provides a good explanation on how to validate the simulation by correct calibration. Depending on the goal of simulation one need either to set the amount of shares offered by imaginary trader higher or lower. High values are good for testing a small investor strategy, while smaller values may be good for testing overall impact of some regulations, allowing to divert from historical flow of price more easily with less agents involved.

Simplest scenario of providing calibration data is giving just SELL offers however in order to simmulate more realistic behaviour it is needed to provide buy and sell orders with spread that is to be determined manualy, as precise data is expensive to gather.

# Hypothesis to be verified

## Testing of a greedy strategy

## Testing how instable may the market become under influence of random traders

