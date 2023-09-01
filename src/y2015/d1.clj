(ns y2015.d1
  (:require [utils :as u]))

(def input (first (u/input)))

; ---

(defn- floor' [floor c]
  (case c
    \( (inc floor)
    \) (dec floor)))

(defn part-1 [s]
  (reduce #(floor' %1 %2) 0 s))

(defn part-2 [s]
  (reduce (fn [{:keys [floor pos]} c]
            (if (neg? floor)
              (reduced pos)
              {:floor (floor' floor c)
               :pos (inc pos)}))
          {:floor 0, :pos 0} s))

(comment
  (part-1 input) ; => 280
  (part-2 input) ; => 1797
)
