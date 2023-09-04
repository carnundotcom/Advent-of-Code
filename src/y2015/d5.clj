(ns y2015.d5
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))


(defn nice? [s]
  (let [pairs (partition 2 1 s)]
    (boolean
      (when (not (some #{'(\a \b) '(\c \d) '(\p \q) '(\x \y)} pairs)) ; no bad pairs
        (when (some (fn [[a b]] (= a b)) pairs) ; some good pair
          (<= 3 (count (filter #{\a \e \i \o \u} s)))))))) ; at least 3 vowels

(defn really-nice? [s]
  (let [triplets (partition 3 1 s)
        pairs (->> (remove (partial apply =) triplets) ; remove 'overlapping' pairs
                   (map (fn [[_ a b]] [a b])) ; stitch back up
                   (cons (take 2 (first triplets))))] ; don't forget first pair!
    (boolean
      (when (some (fn [[a _ b]] (= a b)) triplets) ; one letter between
        (some (fn [[_ v]] (<= 2 v)) (frequencies pairs)))))) ; repeated pair


(tests
  (def test:dummy-1 ["ugknbfddgicrmopn"
                     "aaa"
                     "jchzalrnumimnmhp"
                     "haegwjzuvuyypxyu"
                     "dvszwmarrgswjxmb"])
  (map nice? test:dummy-1) := '(true true false false false)

  (def test:dummy-2 ["qjhvhtzxzqqjkmpb"
                     "xxyxx"
                     "uurcxstgmygtbstg"
                     "ieodomkazucvgmuy"])
  (map really-nice? test:dummy-2) := '(true true false false)
  )

(comment
  (def input (u/input))

  ;; part 1
  (count (filter nice? input)) ; => 258
  ;; part 2
  (count (filter really-nice? input)) ; => 53
  )
