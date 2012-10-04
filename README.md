# DiningPhilosophers

Solution to [Dining Philosophers]http://en.wikipedia.org/wiki/Dining_philosophers_problem 
written in Clojure  by the Toronto Coding Dojo.

Refactored and visualization added by Tom.

## Usage
    lein run

    Switches          Default  Desc                                       
    --------          -------  ----                                       
    -s, --philocount           number of philosophers/forks               
    -n, --times                number of iterations each philosopher eats 

    lein run -s 5 -n 5

Outputs JSON of the events that took place

## Visualization

Some html / svg / javascript to visualize the output is in html/draw.html

## License

Copyright © 2012 Toronto Coding Dojo

Distributed under the Eclipse Public License, the same as Clojure.
