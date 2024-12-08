(ns y2024.d7
  (:require
    [clj-async-profiler.core :as prof]
    [clojure.math.combinatorics :as combo]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input ["190: 10 19"
                  "3267: 81 40 27"
                  "83: 17 5"
                  "156: 15 6"
                  "7290: 6 8 6 15"
                  "161011: 16 10 13"
                  "192: 17 8 14"
                  "21037: 9 7 18 13"
                  "292: 11 6 16 20"])
(def input (u/input))

; ---

(defn parse [input]
  (mapv (fn [line]
          (let [[goal & nums] (->> (re-seq #"\d+" line)
                                   (map parse-long))]
            [goal nums]))
        input))

; ---

(defn apply-ops-v1 [nums ops]
  (let [[n1 n2] (take 2 nums)
        op   (first ops)]
    (if op
      (apply-ops-v1 (conj (drop 2 nums) (op n1 n2)), (rest ops))
      (first nums))))


(defn apply-ops-v2 [nums ops]
  (reduce
    (fn [total [n op]]
      (op total n))
    (apply (first ops) (take 2 nums))
    (map vector (drop 2 nums) (rest ops))))

(defn solve-v1 [input ops apply-ops-fn]
  (let [data (parse input)]
    ; Time solving, not parsing
    (time
      (->> data
           (keep (fn [[goal nums]]
                   (->> (repeat (dec (count nums)) ops)
                        (apply combo/cartesian-product)
                        (some #(when (= goal (apply-ops-fn nums %)) goal)))))
           (apply +)))))

(defn solve-v2 [input ops apply-ops-fn]
  (let [data (parse input)]
    ; Time solving, not parsing
    (time
      (->> data
           (pmap (fn [[goal nums]]
                   (or (->> (repeat (dec (count nums)) ops)
                            (apply combo/cartesian-product)
                            (some #(when (= goal (apply-ops-fn nums %)) goal)))
                       0)))
           (apply +)))))

(def part1-ops [+ *])
(def part2-ops [+ * (fn || [n1 n2] (parse-long (str n1 n2)))])

(tests
  ;; Part One
  (solve-v1 dummy-input part1-ops apply-ops-v1) := 3749
  (solve-v1 dummy-input part1-ops apply-ops-v2) := 3749
  (solve-v2 dummy-input part1-ops apply-ops-v1) := 3749
  (solve-v2 dummy-input part1-ops apply-ops-v2) := 3749

  ;; Part Two
  (solve-v1 dummy-input part2-ops apply-ops-v1) := 11387
  (solve-v1 dummy-input part2-ops apply-ops-v2) := 11387
  (solve-v2 dummy-input part2-ops apply-ops-v1) := 11387
  (solve-v2 dummy-input part2-ops apply-ops-v2) := 11387
  )

(comment
  ;; Part One

  (solve-v1 input part1-ops apply-ops-v1)
  ; => "Elapsed time: 1064.329637 msecs"
  ;    4998764814652

  (solve-v2 input part1-ops apply-ops-v2)
  ; => "Elapsed time: 207.09289 msecs"
  ;    4998764814652
  ;
  ; apply-ops-v1 (recursive) to v2 (reduce) --> ~2x speedup

  (solve-v2 input part1-ops apply-ops-v1)
  ; => "Elapsed time: 440.859929 msecs"
  ;    4998764814652
  ;
  ; So solve-v1 (keep) to v2 (pmap) --> another ~2x speedup


  ;; Part 2

  (solve-v1 input part2-ops apply-ops-v1)
  ; => "Elapsed time: 58092.999535 msecs"
  ;    37598910447546

  (solve-v2 input part2-ops apply-ops-v2)
  ; => "Elapsed time: 11925.897589 msecs"
  ;    37598910447546
  ;
  ; Again, ~2x speedup

  (solve-v2 input part2-ops apply-ops-v1)
  ; => "Elapsed time: 26465.334281 msecs"
  ;    37598910447546
  ;
  ; And another ~2x


  )
