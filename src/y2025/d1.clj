(ns y2025.d1
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["L68" "L30" "R48" "L5" "R60" "L55" "L1" "L99" "R14" "L82"])
(def input (u/input))

; ---

(def start 50)

(defn parse [s]
  (let [[_ sign n] (re-find #"(.)(\d+)" s)]
    (cond-> (parse-long n)
      (= "L" sign) -)))

(defn part1 [input]
  ; Basic idea: reduce over the input, keeping track of both the current number, :n, and a count for the :password
  (:password
    (reduce
      (fn [m s]
        (let [n (mod (+ (:n m) (parse s)) 100)] ; https://en.wikipedia.org/wiki/Modular_arithmetic, yay!
          (cond-> m
            :always   (assoc :n n)
            (zero? n) (update :password inc))))
      {:n start, :password 0}
      input)))

(defn part2 [input]
  ; Basic idea: like part1, except at each step generate *all* nums visited, then count the zeros
  (:password
    (reduce
      (fn [m s]
        (let [delta (parse s)
              nums  (for [d (range 1 (inc (abs delta)))]
                      (mod (+ (:n m) ((if (pos? delta) + -) d)) 100))]
          (-> m
              (assoc :n (last nums))
              (update :password + (count (filter zero? nums))))))
      {:n start :password 0}
      input)))

(tests
  (part1 dummy-input) := 3
  (part2 dummy-input) := 6
  )

(comment
  (part1 input) ; => 1043
  (part2 input) ; => 5963
  )
