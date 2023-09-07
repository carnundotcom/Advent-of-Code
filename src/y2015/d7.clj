(ns y2015.d7
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["123 -> x"
                  "456 -> y"
                  "x AND y -> d"
                  "x OR y -> e"
                  "x LSHIFT 2 -> f"
                  "y RSHIFT 2 -> g"
                  "NOT x -> h"
                  "NOT y -> i"])
(def input (u/input))

; ---

;; gate provides no signal until all inputs have a signal

(do
  (defn parse
    "Returns a map representation of an instruction, s, with keys:
    - :sig -> input signal
    - :w1  -> first input wire
    - :w2  -> second input wire
    - :op  -> operation
    - :out -> output wire"
    [s]
    (let [[a b c d e] (str/split s #" ")]
      (cond
        (= d e nil)                 {:type :sig, :args {:sig a, :out c}}
        (or (= "AND" b) (= "OR" b)) {:type :and-or, :args {:w1 a, :w2 c, :op b :out e}}
        (str/includes? b "SHIFT")   {:type :shift, :args {:w1 a, :sig c, :op b, :out e}}
        (= "NOT" a)                 {:type :not :args {:w1 b, :out d}})))

  ;; Initial idea was a big regex with lots of capturing groups:
  ;(def match-instructions
  ;  (re-pattern
  ;    (str "(?:(^\\d+) -> (\\w+))" ; signal -> wire
  ;         "|(?:(^\\w+) ((?:AND|OR)) (\\w+) -> (\\w+))" ; wire AND/OR wire -> wire
  ;         "|(?:(^\\w+) ((?:LSHIFT|RSHIFT)) (\\d+) -> (\\w+))" ; wire L/RSHIFT signal -> wire
  ;         "|(?:(^NOT) (\\w+) -> (\\w+))")) ; NOT wire -> wire
  ;  )
  ;; For use with some clever sequential destructuring. Too clever! It ended up thwarted by:
  ;(let [[a b a] [:foo :bar :nil] [a b])
  ;=> [nil :bar]

  (map parse dummy-input))



(tests

)

(comment
  (transduce
    (map parse)
    (completing
      (fn [m {t :type, {:keys [sig w1 w2 op out]} :args}]
        (prn m t)
        (case t
          :sig (assoc m out sig)
          :and-or m
          :shift m
          :args m
          "foo")))
    {}
    input)
  )
