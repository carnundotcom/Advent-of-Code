(ns y2025.d3
  (:require
    [clojure.math.combinatorics :as combo]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input ["987654321111111" "811111111111119" "234234234234278" "818181911112111"])
(def input (u/input))

; ---

#_(defn joltage
  "Rough idea: generate all the possible 'joltage' numbers, then pick the largest.

  Works for small n. Farrrrr to slow in general!"
  [n s]
  (apply max
         (for [indices (combo/combinations (range (count s)) n)
               ;:when (apply < indices)
               :let [chs (map #(nth s %) indices)]]
           (parse-long (reduce str "" chs)))))

(defn joltage
  "Rough idea: run over the digits once (!), building up a stack of best-so-far digits, and popping from it when the
  last digit added is smaller than the one in hand; until you run out of 'deletions' per `(- (count s) n)`.

  With thanks to my rubber duck, Claude."
  [n s]
  (loop [digit-chars (seq s)
         best-so-far [] ; stack
         deletions   (- (count s) n)]
    (if (empty? digit-chars)
      ; Run out of digits --> return from best-so-far
      (parse-long (apply str (take n best-so-far)))

      ; Next digit...
      (let [d (parse-long (str (first digit-chars)))]
        (if (and (pos? deletions)
                 (seq best-so-far)
                 (< (peek best-so-far) d))
          ; Make a deletion
          (recur digit-chars (pop best-so-far) (dec deletions))
          ; Add to best-so-far
          (recur (rest digit-chars) (conj best-so-far d) deletions))))))

(defn solve
  [input n]
  (reduce + (map (partial joltage n) input)))

(tests
  (solve dummy-input 2)  := 357
  (solve dummy-input 12) := 3121910778619
  )

(comment
  (solve input 2)  ; => 17324
  (solve input 12) ; => 171846613143331


  ; Some profiling played a role in realising... my initial approach was never going to get fast enough!
  (require '[clj-async-profiler.core :as prof])
  (prof/profile (solve input 2))
  (prof/serve-ui 8080)

  )
