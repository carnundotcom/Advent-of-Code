(ns y2024.d1
  (:require
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input
  ["3   4"
   "4   3"
   "2   5"
   "1   3"
   "3   9"
   "3   3"])

(def input (u/input))

; ----------------------------------------------------------------------------------------------------------------------

(defn parse-line
  [s]
  (let [[_ n1 n2] (re-find #"(\d+)\s+(\d+)" s)]
    [(parse-long n1) (parse-long n2)]))

(defn- parse
  "Returns a vector of two vectors: one for each vertical 'list' of numbers in the input."
  [input]
  (reduce (fn [lists-v s]
            (let [[n1 n2] (parse-line s)]
              (-> lists-v
                  (update 0 conj n1)
                  (update 1 conj n2))))
          [[] []] input))

; ----------------------------------------------------------------------------------------------------------------------

(defn part1 [input]
  (->> (parse input)                          ; e.g. [[3 4 2 1 3 3] [4 3 5 3 9 3]]
       (map sort)                             ;      ((1 2 3 3 3 4) (3 3 3 4 5 9))
       (apply (partial map #(abs (- %1 %2)))) ;      (2 1 0 1 2 5)
       (apply +)))                            ;      11

(defn part2 [input]
  (let [[left right] (parse input)
        right-freqs  (frequencies right)] ; e.g. {4 1, 3 3, 5 1, 9 1}
    (reduce (fn [score n]
              (+ score (* n (get right-freqs n 0))))
            0 left)))

(tests
  (part1 dummy-input) := 11
  (part2 dummy-input) := 31
  )

(comment
  (part1 input) ; => 1970720
  (part2 input) ; => 17191599
  )
