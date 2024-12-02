# Advent of Code

| year | day | summary |
| --- | --- | --- |
| 2015 | [1](src/y2015/d1.clj) | reduuuuuuuce |
| 2015 | [2](src/y2015/d2.clj) | simple regex, yummy destructuring, map/reduce... fun! |
| 2015 | [3](src/y2015/d3.clj) | reduce is very versatile! |
| 2015 | [4](src/y2015/d4.clj) | clojure is plenty fast, eh? |
| 2015 | [5](src/y2015/d5.clj) | maybe helper functions would be more readable? otherwise, simple enough :) |
| 2015 | [6](src/y2015/d6.clj) | had some (but not tooooo much) fun optimising, ha |
| 2015 | [7](src/y2015/d7.clj) | **WIP** |
| | | |
| 2020 | [1](src/y2020/d1.clj) | (just warming up for 2024!) |
| | | |
| 2021 | [1](src/y2021/d1.clj) | off to a nice, neat start |
| 2021 | [2](src/y2021/d2.clj) | sooo much nicer than [my first attempt](https://github.com/CarnunMP/AoC-2021/blob/master/src/days/day2.clj)! |
| 2021 | [3](src/y2021/d3.clj) | not terrible |
| | | |
| 2022 | [1](src/y2022/d1.clj) | let's goooooo! |
| 2022 | [2](src/y2022/d2.clj) | alright: I was a bit distracted :P |
| 2022 | [3](src/y2022/d3.clj) | neat... but feels like the calm before the storm |
| 2022 | [4](src/y2022/d4.clj) | ditto |
| 2022 | [5](src/y2022/d5.clj) | parsing is a little inelegant, but the rest was fine :) |
| 2022 | [6](src/y2022/d6.clj) | piece of piss |
| 2022 | [7](src/y2022/d7.clj) | SO IT BEGINS |
| 2022 | [8](src/y2022/d8.clj) | eh... quite ugly :/ |
| 2022 | [9](src/y2022/d9.clj) | not too bad! |
| 2022 | [10](src/y2022/d10.clj) | neat enough! |
| 2022 | [11](src/y2022/d11.clj) | part 1? fine. part 2? uhhhhh... |
| 2022 | [12](src/y2022/d12.clj) | a little ugly. but more importantly: slowwwww... |
| 2022 | [13](src/y2022/d13.clj) | little hard to understand the spec, but got there in the end |
| 2022 | [14](src/y2022/d14.clj) | easy enough; wordy solution though |
| | | |
| 2023 | [1](src/y2023/d1.clj) | overlapping regex, grrrrr... |
| 2023 | [2](src/y2023/d2.clj) | zero snags; enjoyed making it nice :) |
| 2023 | [3](src/y2023/d3.clj) | easy peeeeez... once I figured how best to parse things |
| 2023 | [4](src/y2023/d4.clj) | neat! |
| | | |
| 2024 | [1](src/y2024/d1.clj) | I think I'll break from, uh, _tradition_ this year and write marginally more serious summaries! :D</br></br>That said, not much to say about this one. Nice warmup. Hopefully I can keep up the readability, among other things, as we go on — because if I had a goal for **AoC 2024** it'd be to produce elegant, readily understandable (especially to [Clojure](https://clojure.org) beginners), _performant_ solutions. In roughly that order.</br></br>I know I'm going to miss some days. But at the very least I hope to keep up, then swing back later to fill in any gaps. There's only so much time in December for 'toy problems' anyhow... which as it happens is a happy constraint. I'm very grateful that [Clojure](https://clojure.org) isn't just a _toy language_ for me, but a living. Perhaps my rushed solutions here can help others into the same predicament? `(repeat "ONE OF US")`</br></br>P.S. Do also check out the [#adventofcode](https://clojurians.slack.com/archives/C0GLTDB2T) channel in Clojurians Slack! There will be daily solution threads. I always learn a lot from them. |
| 2024 | [2](src/y2024/d2.clj) | This one was fun! My initial approach to **Part One** — based on partitioning each 'report' into pairs, then checking the first pair to determine whether the rest of the report should increase, decrease, or had already failed — didn't seamlessly scale to **Part Two**, so I had to go back to the drawing board a bit. What I ended up with was one big [reduce](https://clojuredocs.org/clojure.core/reduce) that first _scores_ each report based on number of increasing pairs, number of decreasing pairs, and number of 'bounded' pairs; checks whether a score is 'safe' (i.e. the bounded total, plus either the increasing or decreasing total, equals the number of pairs); then if it *isn't* (and a `retry?` arg is true), runs (lazily) over the possible alternate reports missing one element until either a safe alternate is found, or there are no more alternates to test.</br></br>I had worried about this resulting in needlessly, repeatedly scoring the same pairs, so I [memoize](https://clojuredocs.org/clojure.core/memoize)d the `score-pair` function. But this didn't seem to make any measurable difference to performance — plenty fast in either case. Perhaps with much larger 'reports', or a 'tolerance' higher than one bad element, things would get more interesting. :) |

Feedback welcome!
