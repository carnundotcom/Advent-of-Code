(ns y2024.d2
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["7 6 4 2 1"
                  "1 2 7 8 9"
                  "9 7 6 2 1"
                  "1 3 2 4 5"
                  "8 6 4 4 1"
                  "1 3 6 7 9"])
(def input (u/input))

; ----------------------------------------------------------------------------------------------------------------------

(defn parse-line
  [s]
  (map parse-long (re-seq #"\d+" s)))

; ----------------------------------------------------------------------------------------------------------------------

(defn- inc?
  "Returns 1 if the pair increases, else 0."
  [[n1 n2]]
  (if (< n1 n2) 1 0))

(defn- dec?
  "Returns 1 if the pair decreases, else 0."
  [[n1 n2]]
  (if (> n1 n2) 1 0))

(defn- bounded?
  "Returns 1 if n1 and n2 differ by at least one and at most 3."
  [[n1 n2]]
  (if (<= 1 (abs (- n1 n2)) 3) 1 0))

(def score-pair
  (memoize
    (fn [pair]
      {:inc (inc? pair)
       :dec (dec? pair)
       :bounded (bounded? pair)
       :total 1})))

(defn score-pairs
  [pairs]
  (reduce (fn [scores pair]
            (merge-with + scores (score-pair pair)))
          {} pairs))

(defn safe?
  "Returns true if the report is 'safe', else false."
  [report]
  (let [{:keys [inc dec _same bounded total]}
        (score-pairs (partition 2 1 report))]
    (and (= bounded total)
         (or (= inc total)
             (= dec total)))))

(defn remove-nth
  "Returns a new coll without the nth element."
  [coll n]
  (concat (take n coll) (drop (inc n) coll)))

(defn solve
  [input retry?]
  (reduce
    (fn [total-safe line]
      (let [report (parse-line line)]
        (cond
          ; A. The current report is 'safe' --> increment the total
          (safe? report)
          (inc total-safe)

          ; B. The current report is 'unsafe', and retry? is true
          ;      --> try to find a 'safe' report with one level missing
          ;          if so, increment the total
          ;          else, don't
          retry?
          (if (some safe? (for [n (range (count report))] (remove-nth report n)))
            (inc total-safe)
            total-safe)

          ; C. The current report is 'unsafe' --> leave the total alone
          :else
          total-safe)))
    0 ; <-- total-safe init value
    input))

; ----------------------------------------------------------------------------------------------------------------------

(tests
  (solve dummy-input false) := 2
  (solve dummy-input true) := 4
  )

(comment
  (solve input false) ; => 479
  (solve input true)  ; => 531
  )
