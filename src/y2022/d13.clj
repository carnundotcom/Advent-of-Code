(ns y2022.d13
  (:require [clojure.edn :as edn]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["[1,1,3,1,1]"
                  "[1,1,5,1,1]"
                  ""
                  "[[1],[2,3,4]]"
                  "[[1],4]"
                  ""
                  "[9]"
                  "[[8,7,6]]"
                  ""
                  "[[4,4],4,4]"
                  "[[4,4],4,4,4]"
                  ""
                  "[7,7,7,7]"
                  "[7,7,7]"
                  ""
                  "[]"
                  "[3]"
                  ""
                  "[[[]]]"
                  "[[]]"
                  ""
                  "[1,[2,[3,[4,[5,6,7]]]],8,9]"
                  "[1,[2,[3,[4,[5,6,0]]]],8,9]"])
(def input (u/input))

; ---

(defn pairs [input]
  (->> (partition 3 3 '("") input)
       (map (fn [[a b _]]
              [(edn/read-string a)
               (edn/read-string b)]))))


(defn correct-order? [[left right]]
  (cond
    (and (int? left) (int? right))
    (cond
      (< left right) :true
      (> left right) :false)

    (and (vector? left) (vector? right))
    (or (some correct-order? (map vector left right))
        (cond
          (< (count left) (count right)) :true
          (> (count left) (count right)) :false))

    (int? left) (correct-order? [[left] right])
    (int? right) (correct-order? [left [right]])))


(defn part-1 [input]
  (->> (pairs input)
       (map-indexed (fn [i pair]
                      ;(prn "------------")
                      ;(prn "pair" (inc i) pair)
                      ;(prn (correct-order? pair))
                      [(inc i) (correct-order? pair)]))
       (reduce (fn [sum [i correct]] (if (= :true correct) (+ sum i) sum))
               0)))


(defn- compare-packets [left right]
  (condp = (correct-order? [left right])
    ;; explicitly translate to comparator semantics
    :true -1
    :false 1
    nil 0))

(defn part-2 [input]
  (->> (pairs input)
       (apply concat [[[2]]] [[[6]]])
       (sort-by identity compare-packets)
       (map-indexed (fn [i packet] [(inc i) packet]))
       (reduce (fn [first-index [i packet]]
                 (condp = packet
                   [[2]] i
                   [[6]] (* first-index i)
                   first-index)))))


(tests
  (map correct-order? (pairs dummy-input)) := '(:true :true :false :true :false :true :false :false)
  (part-1 dummy-input) := 13
  (part-2 dummy-input) := 140
  )

(comment
  (part-1 input) ; => 6420
  (part-2 input) ; => 22000
  )
