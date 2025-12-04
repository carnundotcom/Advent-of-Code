(ns y2025.d4
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input
  [[\. \. \@ \@ \. \@ \@ \@ \@ \.]
   [\@ \@ \@ \. \@ \. \@ \. \@ \@]
   [\@ \@ \@ \@ \@ \. \@ \. \@ \@]
   [\@ \. \@ \@ \@ \@ \. \. \@ \.]
   [\@ \@ \. \@ \@ \@ \@ \. \@ \@]
   [\. \@ \@ \@ \@ \@ \@ \@ \. \@]
   [\. \@ \. \@ \. \@ \. \@ \@ \@]
   [\@ \. \@ \@ \@ \. \@ \@ \@ \@]
   [\. \@ \@ \@ \@ \@ \@ \@ \@ \.]
   [\@ \. \@ \. \@ \@ \@ \. \@ \.]])
(def input (u/input {:as :grid}))

; ---

(defn- adj-roll-positions
  [grid pos]
  (filter #(= \@ (get-in grid %)) (u/adj-positions grid pos)))

(defn removable-positions
  [grid]
  (seq
    (reduce
      (fn [acc pos]
        (if (and (= \@ (get-in grid pos))
                 (> 4 (count (adj-roll-positions grid pos))))
          (conj acc pos)
          acc))
      '()
      (u/grid->positions grid))))

(defn part1 [input]
  (count (removable-positions input)))

(defn part2 [input]
  (count
    (loop [grid    input
           removed '()]
      (if-let [rp (removable-positions grid)]
        (recur
          ; Remove the rolls
          (reduce (fn [grid pos] (assoc-in grid pos \.)) grid rp)
          ; Keep track of rolls removed
          (concat removed rp))

        ; Return
        removed))))

(tests
  (part1 dummy-input) := 13
  (part2 dummy-input) := 43
  )

(comment
  (part1 input) ; => 1372
  (part2 input) ; => 7922
  )
