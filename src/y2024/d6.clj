(ns y2024.d6
  (:require
    [clojure.core.matrix :as m]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input
  [[\. \. \. \. \# \. \. \. \. \.]
   [\. \. \. \. \. \. \. \. \. \#]
   [\. \. \. \. \. \. \. \. \. \.]
   [\. \. \# \. \. \. \. \. \. \.]
   [\. \. \. \. \. \. \. \# \. \.]
   [\. \. \. \. \. \. \. \. \. \.]
   [\. \# \. \. \^ \. \. \. \. \.]
   [\. \. \. \. \. \. \. \. \# \.]
   [\# \. \. \. \. \. \. \. \. \.]
   [\. \. \. \. \. \. \# \. \. \.]])
(def input (u/input {:as :grid}))

; ----------------------------------------------------------------------------------------------------------------------

(def guard? #{\^ \> \v \<})

(defn out-of-bounds?
  [grid [row col]]
  (or (< row 0) (>= row (count grid))
      (< col 0) (>= col (count (first grid)))))

(defn step-or-turn
  "Returns [<old direction>, <new pos>] for steps, and [<new direction>, <old pos>] for turns."
  [grid guard-dir guard-pos]
  (let [[row' col'] (m/add guard-pos (u/dir->x+y guard-dir))]
    (if (= (get-in grid [col' row']) \#)
      ; Turn
      [(u/turn-right guard-dir) guard-pos]
      ; Step
      [guard-dir [row' col']])))

(defn loop?
  [grid guard-dir guard-pos [x y]]
  (let [grid (assoc-in grid [y x] \#)]
    (loop [visited? #{} ; [dir pos] tuples
           dir guard-dir
           pos guard-pos]
      (cond
        (out-of-bounds? grid pos) 0
        (visited? [dir pos])      1
        :else                     (let [[dir' pos'] (step-or-turn grid dir pos)]
                                    (recur (conj visited? [dir pos]) dir' pos'))))))

(defn all-positions
  [grid]
  (let [[col row] (u/pos-in-grid grid guard?)
        start-dir (case (get-in grid [row col]) \^ :n, \> :e, \v :s, :w \<)]
    (reduce
      (fn [{:keys [dir pos visited] :as m} _]
        (if (out-of-bounds? grid pos)
          ; Return visited - this out-of-bounds pos
          (reduced (disj visited pos))

          ; Else, continue
          (let [[dir' pos'] (step-or-turn grid dir pos)]
            (-> m
                (assoc :dir dir', :pos pos')
                (update :visited conj pos')))))
      {:dir     start-dir
       :pos     [col row]
       :visited #{[col row]}}
      (range))))

; ----------------------------------------------------------------------------------------------------------------------

(defn part-1 [grid]
  (count (all-positions grid)))

(defn part-2 [grid]
  (let [[col row] (u/pos-in-grid grid guard?)
        start-dir (case (get-in grid [row col]) \^ :n, \> :e, \v :s, :w \<)]
    (reduce (fn [n [col' row']]
              (+ n (loop? grid start-dir [col row] [col' row'])))
            0 (all-positions grid))))

(tests
  (u/pos-in-grid dummy-input guard?) := [4 6]
  (part-1 dummy-input) := 41
  (part-2 dummy-input) := 6)

(comment
  (part-1 input)
  ; => 4665

  (time
    (part-2 input))
  ; "Elapsed time: 41281.940127 msecs"
  ; => 1688

  ; Oof. Brute-forcing it is slowwww! Who would have guessed? :D

  ; Some ideas that would probably speed things up a lot (that I'm too lazy to code up right now):
  ;  - Instead of checking all-positions visited, check only suitable 'candidate' positions — i.e. positions next to
  ;    intersection points of the path from Part 1. (?)
  ;  - Instead of checking *from the beginning of the guard's patrol* for each candidate, check from just before he
  ;    would hit it.
  )
