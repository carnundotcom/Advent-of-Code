(ns y2025.d2
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(def dummy-input
  "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124")
(def input
  (slurp "data/y2025/d2.txt"))

; ---

(defn part1
  "Returns true if the first half of n's digits equal the second, else false."
  [n]
  (let [s (str n)
        c (count s)]
    (when (even? c)
      (= (subs s 0 (/ c 2))
         (subs s (/ c 2))))))

(defn part2
  "Returns true if there is some partition of n's digits such that every partition is equal, else false."
  [n]
  (let [s (str n)
        c (count s)]
    ; Neat 'trick' if you haven't yet come across it: a *terminating* reduction, with `reduced`
    (reduce
      (fn [_ step]
        (if (apply = (partition step step nil s))
          (reduced true)
          false))
      nil
      (range 1 (inc (int (/ c 2)))))))

(defn solve [invalid? input]
  (->> (str/split input #",|-|\n")
       (map parse-long)
       (partition 2 2)
       (mapcat (fn [[start end]] (range start (inc end))))
       (filter invalid?)
       (reduce +)))

(tests
  (solve part1 dummy-input) := 1227775554
  (solve part2 dummy-input) := 4174379265
  )

(comment
  (solve part1 input) ; => 35367539282
  (solve part2 input) ; => 45814076230
  )
