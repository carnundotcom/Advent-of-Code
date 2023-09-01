(ns y2015.d2
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def input (u/input))

; ---

(defn parse [input]
  (map (fn [s]
         (let [[_ & dims] (re-find #"(\d+)x(\d+)x(\d+)" s)]
           (mapv parse-long dims)))
       input))

(defn paper [[l w h]]
  (let [sides [(* l w) (* w h) (* h l)]]
    (+ (* 2 (apply + sides))
       (apply min sides))))

(defn ribbon [[l w h]]
  (let [perimeters [(+ l l w w) (+ w w h h) (+ h h l l)]]
    (+ (apply min perimeters)
       (* l w h))))

(tests
  (first (parse input)) := [3 11 24]
  (paper [2 3 4]) := 58
  (paper [1 1 10]) := 43
  (ribbon [2 3 4]) := 34
  (ribbon [1 1 10]) := 14
  )

(comment
  (defn solve [f input]
    (->> (parse input)
         (map f)
         (reduce +)))

  (solve paper input) ; => 1588178
  (solve ribbon input) ; => 3783758
  )
