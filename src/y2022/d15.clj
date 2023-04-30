(ns y2022.d15
  (:require [clojure.set :refer [union]]
            ; ---
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["Sensor at x=2, y=18: closest beacon is at x=-2, y=15"
                  "Sensor at x=9, y=16: closest beacon is at x=10, y=16"
                  "Sensor at x=13, y=2: closest beacon is at x=15, y=3"
                  "Sensor at x=12, y=14: closest beacon is at x=10, y=16"
                  "Sensor at x=10, y=20: closest beacon is at x=10, y=16"
                  "Sensor at x=14, y=17: closest beacon is at x=10, y=16"
                  "Sensor at x=8, y=7: closest beacon is at x=2, y=10"
                  "Sensor at x=2, y=0: closest beacon is at x=2, y=10"
                  "Sensor at x=0, y=11: closest beacon is at x=2, y=10"
                  "Sensor at x=20, y=14: closest beacon is at x=25, y=17"
                  "Sensor at x=17, y=20: closest beacon is at x=21, y=22"
                  "Sensor at x=16, y=7: closest beacon is at x=15, y=3"
                  "Sensor at x=14, y=3: closest beacon is at x=15, y=3"
                  "Sensor at x=20, y=1: closest beacon is at x=15, y=3"])
(def input (u/input))

; ---

(defn manhattan-distance [x1 y1 x2 y2]
  (+ (abs (- x2 x1))
     (abs (- y2 y1))))

(defn parse [input]
  (mapv
    #(let [[x1 y1 x2 y2] (->> (re-seq #"(?<=[x|y]=)[-|\d]+" %)
                              (map parse-long))]
       {:s [x1 y1]
        :b [x2 y2]
        :md (manhattan-distance x1 y1 x2 y2)})
    input))

; ---

(defn points-in-row-from-s [y {[sx sy :as s] :s, b :b, md :md}]
  (let [yd (abs (- y sy))
        n (-> (- md yd) (* 2) inc)
        n (if (pos? n) n 0)
        r (/ (dec n) 2)]
    (-> (for [x (range (- sx r) (+ sx r 1))] [x y])
         set
         (disj s b))))

(defn part-1 [y input]
  (->> (parse input)
       (reduce #(union %1 (points-in-row-from-s y %2)) #{})
       count))

(tests
  (def info (parse dummy-input))

  (first info) := {:s [2 18], :b [-2 15], :md 7}
  (points-in-row-from-s 15 (nth info 6)) := #{[7 15] [8 15] [9 15]}
  (part-1 10 dummy-input) := 26
  )

(comment
  (part-1 2000000 input) ; => 4861076
)
