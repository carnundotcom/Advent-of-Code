(ns y2022.d3
  (:require [clojure.set :as set]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["vJrwpWtwJgWrhcsFMMfFFhFp"
                  "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL"
                  "PmmdzqPrVvPwwTWBwg"
                  "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn"
                  "ttgJtRGJQctTZtZT"
                  "CrZsJsPPZsGzwwsLwLmpwMDw"])

(def input (u/input))

; ---

(def char->priority
  (->> (concat (range (int \a) (inc (int \z)))
               (range (int \A) (inc (int \Z))))
       (map-indexed (fn [i c] [(char c) (inc i)]))
       (into {})))

(defn priority [partitions]
  (->> (map set partitions)
       (apply set/intersection)
       first
       char->priority))

(defn part-1 [input]
  (->> (map #(priority (partition (/ (count %) 2) %)) input)
       (reduce +)))

(defn part-2 [input]
  (->> (partition 3 input)
       (reduce #(+ %1 (priority %2)) 0)))

(tests
  (part-1 dummy-input) := 157
  (part-2 dummy-input) := 70)

(comment
  (part-1 input) ; => 8105
  (part-2 input) ; => 2363
  )
