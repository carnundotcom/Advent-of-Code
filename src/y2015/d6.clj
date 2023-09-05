(ns y2015.d6
  (:require [utils :as u]))


(def input (u/input))

; ---

(defn point->ops
  "Reads an instruction, s, and produces a map of points to 'ops' i.e. functions to apply, successively, to (some
  representation of) an unlit light at that point.

  A translation of the original 'toggle', 'turn on', and 'turn off' operations must be supplied (e.g. in map form)."
  [s ops']
  (let [[_ op & coords] (re-find #"([\w| ]+) (\d+),(\d+) through (\d+),(\d+)" s)
        op (ops' op)
        [x1 y1 x2 y2] (map parse-long coords)]
    (-> (for [x (range (min x1 x2) (inc (max x1 x2)))
              y (range (min y1 y2) (inc (max y1 y2)))]
          [x y])
        (zipmap (repeat [op])))))

(defn solve [input part]
  (let [ops' (case part
               :part-1 {"toggle" not
                        "turn on" (constantly true)
                        "turn off" (constantly false)}
               :part-2 {"toggle" (partial + 2)
                        "turn on" inc
                        "turn off" #(max 0 (dec %))})
        grid-history (loop [grid {}
                            s input]
                       (let [[s & tail] s]
                         (if-not s grid
                           (recur (merge-with into grid (point->ops s ops'))
                                  tail))))]
    (reduce (fn [res [_point ops]]
              (let [light-now (reduce (fn [res f]
                                        (f res))
                                      (case part
                                        :part-1 false
                                        :part-2 0) ops)]
                (case part
                  :part-1 (if light-now (inc res) res)
                  :part-2 (+ res light-now))))
            0 grid-history)))

(comment
  (time (solve input :part-1))
  ;;="Elapsed time: 30647.259124 msecs"
  ;543903

  (time (solve input :part-2))
  ;;="Elapsed time: 30415.081502 msecs"
  ;14687245

  ;; nicely general! but not super efficient...
  )
