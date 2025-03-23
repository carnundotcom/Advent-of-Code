(ns y2024.d8
  (:require [hyperfiddle.rcf :refer [tests]]
            [clojure.math.combinatorics :as combo]
            [clojure.pprint :refer [pprint]]
            [utils :as u]))

(def dummy-input [[\. \. \. \. \. \. \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \. \0 \. \. \.]
                  [\. \. \. \. \. \0 \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \0 \. \. \. \.]
                  [\. \. \. \. \0 \. \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \A \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \. \A \. \. \.]
                  [\. \. \. \. \. \. \. \. \. \A \. \.]
                  [\. \. \. \. \. \. \. \. \. \. \. \.]
                  [\. \. \. \. \. \. \. \. \. \. \. \.]])
(def input (u/input {:as :grid}))

; ----------------------------------------------------------------------------------------------------------------------

(defn- doto-print-grid
  "Prints grid + antinodes, as in the problem statement, then returns antinodes. For debugging."
  [grid antinodes]
  (let [antinode? (set antinodes)]
    (pprint
      (reduce (fn [grid [row col]]
                (let [ch (get-in grid [row col])]
                  (if (and (= \. ch) (antinode? [row col]))
                    (assoc-in grid [row col] \#)
                    grid)))
              grid (u/grid->positions grid))))

  antinodes)

; ----------------------------------------------------------------------------------------------------------------------

(defn- antinodes
  [bounded? order positions]
  (let [start (if (= ##Inf order) 0 order)]
    (->> (combo/combinations positions 2) ; all possible pairs of positions
         (reduce (fn [antinodes [[row1 col1] [row2 col2]]]
                   (let [row-diff (- row2 row1)
                         col-diff (- col2 col1)]
                     (concat antinodes
                             ; antinodes back from the first in the pair
                             (for [i (range start ##Inf)
                                   :let [pos [(- row1 (* row-diff i)) (- col1 (* col-diff i))]]
                                   :while (and (<= i order) (bounded? pos))]
                               pos)
                             ; antinodes forward from the second in the pair
                             (for [i (range start ##Inf)
                                   :let [pos [(+ row2 (* row-diff i)) (+ col2 (* col-diff i))]]
                                   :while (and (<= i order) (bounded? pos))]
                               pos))))
                 []))))

(defn- freq-positions
  "Returns a map from 'frequency' character (e.g. \\A) to its [row col] positions in grid."
  [grid]
  (->> (u/grid->positions grid)
       (reduce (fn [m pos]
                 (let [freq (get-in grid pos)]
                   (if (not= \. freq)
                     (update m freq (fnil conj #{}) pos)
                     m)))
               {})))

; ----------------------------------------------------------------------------------------------------------------------

(defn solve [grid & {:keys [part]}]
  (let [row-max  (dec (count grid))
        col-max  (dec (count (first grid)))
        bounded? (fn [[row col]] (and (<= 0 row row-max) (<= 0 col col-max)))]
    (-> (freq-positions grid)                                               ; {char #{[row col], [row col], ...}, ...}
        (update-vals (partial antinodes bounded? (case part 1 1, 2 ##Inf))) ;  --> turn vals ^here into antinodes
        (vals)
        (->> (apply concat))
        ;(->> (doto-print-grid grid))
        (set)
        (count))))

(tests
  (solve dummy-input {:part 1}) := 14
  (solve dummy-input {:part 2}) := 34
  )

(comment
  (solve input {:part 1}) ; => 364
  (solve input {:part 2}) ; => 1231
  )
