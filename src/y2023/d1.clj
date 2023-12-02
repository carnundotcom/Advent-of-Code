(ns y2023.d1
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input-1 ["1abc2" "pqr3stu8vwx" "a1b2c3d4e5f" "treb7uchet"])
(def dummy-input-2 ["two1nine"
                    "eightwothree"
                    "abcone2threexyz"
                    "xtwone3four"
                    "4nineeightseven2"
                    "zoneight234"
                    "7pqrstsixteen"])
(def input (u/input))

; ---

(defn s->d [s]
  (get {"one"   "1"
        "two"   "2"
        "three" "3"
        "four"  "4"
        "five"  "5"
        "six"   "6"
        "seven" "7"
        "eight" "8"
        "nine"  "9"}
       s s))

(defn solve [input & {:keys [part]}]
  (let [re (case part
             1 #"(\d)"
             ;; overlapping regex, grrrrr...
             2 #"(?=(\d|one|two|three|four|five|six|seven|eight|nine))")]
    (reduce (fn [sum s]
              (->> (re-seq re s)
                   (map (comp s->d second))
                   ((juxt first last))
                   (apply str)
                   (parse-long)
                   (+ sum)))
            0 input)))

(tests
  (solve dummy-input-1 :part 1) := 142
  (solve dummy-input-2 :part 2) := 281
  )

(comment
  (solve input :part 1) ; => 55971
  (solve input :part 2) ; => 54719
  )
