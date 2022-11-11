(ns y2021.d1
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input '(199 200 208 210 200 207 240 269 260 263))
(def input (map parse-long (u/input)))

; ---

(defn part-1 [input]
  (->> (partition 2 1 input)
       (filter (fn [[a b]] (< a b)))
       count))

(tests
  (part-1 dummy-input) := 7)

(comment
  (part-1 input) ; => 1692
  )

; ---

(defn part-2 [input]
  (->> (partition 3 1 input)
       (map (partial reduce +))
       part-1))

(tests
  (part-2 dummy-input) := 5)

(comment
  (part-2 input) ; => 1724
  )
