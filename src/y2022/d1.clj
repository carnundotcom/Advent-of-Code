(ns y2022.d1
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["1000" "2000" "3000" "" "4000" "" "5000" "6000" "" "7000" "8000" "9000" "" "10000"])
(def input (u/input))

; ---

(defn- sorted-calories [input]
  (->> (partition-by (partial = "") input)
       (remove (partial = '("")))
       (map #(reduce (fn [sum line] (+ sum (parse-long line))) 0 %))
       sort))

(defn part-1 [input]
  (last (sorted-calories input)))

(tests
  (def test:cals (sorted-calories dummy-input))
  (first test:cals) := 4000
  (last test:cals) := 24000
  (part-1 dummy-input) := 24000)

(comment
  (part-1 input) ; => 70509
  )

; ---

(defn part-2 [input]
  (let [cals (sorted-calories input)]
    (->> (drop (- (count cals) 3) cals)
         (apply +))))

(tests
  (part-2 dummy-input) := 45000)

(comment
  (part-2 input) ; => 208567
  )
