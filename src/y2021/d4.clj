(ns y2021.d4
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1"
                  ""
                  "22 13 17 11  0"
                  " 8  2 23  4 24"
                  "21  9 14 16  7"
                  " 6 10  3 18  5"
                  " 1 12 20 15 19"
                  ""
                  " 3 15  0  2 22"
                  " 9 18 13 17  5"
                  "19  8  7 25 23"
                  "20 11 10 24  4"
                  "14 21 16 12  6"
                  ""
                  "14 21 17 24  4"
                  "10 16 15  9 19"
                  "18  8 23 26 20"
                  "22 11 13  6  5"
                  " 2  0 12  3  7"])
(def input (u/input))

; ---

(defn- parse-board [board]
  (->> board
       (map-indexed (fn [x row]
                      (map-indexed (fn [y v]
                                     [v {:x x :y y}])
                                   row)))
       (apply concat)
       (into {})))

(defn parse-input [[draws _ & boards]]
  {:draws (->> (str/split draws #",")
               (map parse-long))
   :boards (->> (partition-by str/blank? boards)
                (remove #(= '("") %))
                (mapv (fn [board]
                        (->> board
                             (map #(->> (str/split % #"\s+")
                                        (remove str/blank?)
                                        (map parse-long)))
                             parse-board))))})

(tests
  (def parsed-dummy-input (parse-input dummy-input))
  (:draws parsed-dummy-input) := '(7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1)
  (get-in parsed-dummy-input [:boards 0 22]) := {:x 0 :y 0}
  (get-in parsed-dummy-input [:boards 0 18]) := {:x 3 :y 3}
  (get-in parsed-dummy-input [:boards 0 5]) := {:x 3 :y 4})

;; draw until winning board
;; sum all unmarked nums on winning board
;; multiply by last num drawn

;; need fast access to board positions of nums
;; need fast marked? check

;; could build map from num to [x y] position, for each board
;; even, num -> {:x, :y, :marked?}
;; simple, right?
