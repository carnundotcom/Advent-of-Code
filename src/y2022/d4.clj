(ns y2022.d4
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["2-4,6-8" "2-3,4-5" "5-7,7-9" "2-8,3-7" "6-6,4-6" "2-6,4-8"])
(def input (u/input))

; ---

(defn parse [input]
  (map (fn [s]
         (->> (str/split s #",")
              (mapv #(->> (str/split % #"-")
                          (mapv parse-long)))))
       input))

(defn contained? [[[a1 b1] [a2 b2]]]
  (or (and (<= a1 a2) (>= b1 b2))
      (and (<= a2 a1) (>= b2 b1))))

(defn overlapping? [[[a1 b1] [a2 b2]]]
  (or (and (<= a2 b1) (>= b2 a1))
      (and (<= a1 b2) (>= b1 a2))))

(defn total [input part]
  (->> (parse input)
       (map (condp = part
             :part-1 contained?
             :part-2 overlapping?))
       (filter true?)
       count))

(tests
  (total dummy-input :part-1) := 2
  (total dummy-input :part-2) := 4)

(comment
  (total input :part-1) ; => 582
  (total input :part-2) ; => 893
  )
