(ns y2025.d5
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input
  ["3-5"
   "10-14"
   "16-20"
   "12-18"
   ""
   "1"
   "5"
   "8"
   "11"
   "17"
   "32"])
(def input (u/input))

; ---

(defn- overlap? [[left1 right1] [left2 right2]]
  (or (<= left1 left2 right1)
      (>= right1 right2 left1)))

(defn- combine-ranges
  [ranges]
  (list (apply min (map first ranges))
        (apply max (map second ranges))))

(defn- reduce-ranges [ranges range']
  (let [{overlaps true, rest-ranges false} (group-by (partial overlap? range') ranges)]
    (if (seq overlaps)
      (conj rest-ranges (combine-ranges (conj overlaps range')))
      (conj ranges range'))))

(defn parse [input]
  (let [[range-strs _ ids] (partition-by (comp boolean seq) input)]
    {:ranges (->> (map #(map parse-long (str/split % #"-")) range-strs)
                  (reduce reduce-ranges '()))
     :ids    (map parse-long ids)}))

; ---

(defn part1
  [input]
  (let [{:keys [ranges ids]} (parse input)
        valid?               (fn [ranges id] (some (fn [[left right]] (<= left id right)) ranges))]
    (count (filter (partial valid? ranges) ids))))

(defn part2
  [input]
  (let [{:keys [ranges]} (parse input)]
    (reduce (fn [n [left right]] (+ n (inc (- right left)))) 0 ranges)))

(tests
  (part1 dummy-input) := 3
  (part2 dummy-input) := 14
  )

(comment
  (require '[clj-async-profiler.core :as prof])

  (prof/profile (time (dotimes [_ 100] (part2 dummy-input))))
  (prof/serve-ui 8080)

  (time (part2 input))
)
