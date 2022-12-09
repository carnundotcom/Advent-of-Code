(ns y2022.d9
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input-1 ["R 4" "U 4" "L 3" "D 1" "R 4" "D 1" "L 5" "R 2"])
(def dummy-input-2 ["R 5" "U 8" "L 8" "D 3" "R 17" "D 10" "L 25" "U 20"])
(def input (u/input))

; ---

(def dir->v
  {"R" [1 0]
   "L" [-1 0]
   "U" [0 -1]
   "D" [0 1]})

(defn catch-up
  "Moves the tail to catch up with the head, if necessary."
  [t-pos h-pos]
  (let [sub (m/sub t-pos h-pos)
        adj? (every? #(<= % 1) (m/abs sub))
        delta (if-not adj?
                (condp = sub
                  [0 2] [0 -1] ; move up
                  [0 -2] [0 1] ; move down
                  [2 0] [-1 0] ; move left
                  [-2 0] [1 0] ; move right
                  [1 -2] [-1 1] ; move SW
                  [2 -1] [-1 1]
                  [2 -2] [-1 1]
                  [2 1] [-1 -1] ; move NW
                  [1 2] [-1 -1]
                  [2 2] [-1 -1]
                  [-1 2] [1 -1] ; move NE
                  [-2 1] [1 -1]
                  [-2 2] [1 -1]
                  [-2 -1] [1 1] ; move SE
                  [-1 -2] [1 1]
                  [-2 -2] [1 1])
                [0 0])]
    (m/add t-pos delta)))

(defn solve
  ([input] (solve input 2))
  ([input rope-length]
   (loop [dirs (mapcat #(let [[dir n] (str/split % #" ")]
                          (repeat (parse-long n) dir))
                       input)
          rope (repeat rope-length [0 0])
          visited #{}]
     (let [[dir & dirs] dirs]
       (if dir
         (let [h-pos (m/add (first rope) (dir->v dir))
               rope (reduce (fn [segments pos]
                                  (conj segments (catch-up pos (last segments))))
                                [h-pos]
                                (rest rope))]
           (recur dirs rope (conj visited (last rope))))
         (count visited))))))

(tests
  (solve dummy-input-1) := 13
  (solve dummy-input-1 10) := 1
  (solve dummy-input-2 10) := 36)

(comment
  (solve input) ; => 6522
  (solve input 10) ; => 2717
  )
