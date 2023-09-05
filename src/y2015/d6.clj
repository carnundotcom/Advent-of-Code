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
        ;(loop [x (min x1 x2)
        ;         y (min y1 y2)
        ;         points '()]
        ;    (cond
        ;      (> x (max x1 x2)) (recur (min x1 x2) (inc y) points)
        ;      (> y (max y1 y2)) points
        ;      :else (recur (inc x) y (conj points [x y]))))
        (zipmap (repeat #_[op] op)))))

(defn solve [input part]
  (let [ops' (case part
               :part-1 {"toggle" not
                        "turn on" (constantly true)
                        "turn off" (constantly false)}
               :part-2 {"toggle" (partial + 2)
                        "turn on" inc
                        "turn off" #(max 0 (dec %))})
        _ (prn "generating grid history")
        grid-history (time
                       (loop [grid {}
                              s input]
                         (let [[s & tail] s]
                           (if-not s grid
                             (recur ;(merge-with into grid (point->ops s ops'))
                                    (reduce (fn [grid' [point op]]
                                              (update grid' point (fnil conj []) op))
                                            grid (point->ops s ops'))
                                    tail)))))]
    (prn "reducing grid history")
    (time
      (reduce (fn [res [_point ops]]
                (let [light-now (reduce (fn [res f]
                                          (f res))
                                        (case part
                                          :part-1 false
                                          :part-2 0) ops)]
                  (case part
                    :part-1 (if light-now (inc res) res)
                    :part-2 (+ res light-now))))
              0 grid-history))))

(defn solve' [input part]
  (let [ops (case part
              :part-1 {"toggle" not
                       "turn on" (constantly true)
                       "turn off" (constantly false)}
              :part-2 {"toggle" (partial + 2)
                       "turn on" inc
                       "turn off" #(max 0 (dec %))})]
    (->> (reduce (fn [grid instruction]
                   (let [[_ op & coords] (re-find #"([\w| ]+) (\d+),(\d+) through (\d+),(\d+)" instruction)
                         op (get ops op)
                         ; ---
                         [x1 y1 x2 y2] (map parse-long coords)
                         [x1 x2] [(min x1 x2) (max x1 x2)]
                         [y1 y2] [(min y1 y2) (max y1 y2)]
                         ; ---
                         f (case part
                             :part-1 (fnil op false)
                             :part-2 (fnil op 0))]
                     (loop [x x1
                            y y1
                            grid' grid]
                       (cond
                         (> y y2) grid'
                         (>= x x2) (recur x1 (inc y) (update grid' [x y] f))
                         :else    (recur (inc x) y (update grid' [x y] f))))))
                 {} input)
         (keep (fn [[_ v]] (get {true 1, false 0} v v)))
         (reduce + 0))))


(comment
  (time (solve input :part-1))
  ;;="Elapsed time: 30647.259124 msecs"
  ;543903

  (time (solve input :part-2))
  ;;="Elapsed time: 30415.081502 msecs"
  ;14687245

  ;; Nicely general! But not super efficient...

  ;; Whoa! Generating the grid history takes 99% of the time! Check part 1:
  ; "generating grid history"
  ; "Elapsed time: 29490.599509 msecs"
  ; "reducing grid history"
  ; "Elapsed time: 342.514187 msecs"
  ; 543903

  ;; Betcha the merge-with is to blame, hmm...
  ;; So what about another reduce instead?

  ;; Not bad:
  ; "generating grid history"
  ; "Elapsed time: 20952.709645 msecs"
  ; "reducing grid history"
  ; "Elapsed time: 337.01144 msecs"
  ; 543903

  ;; Hmm. Now I'm wondering if it'd be worth replacing the `for` in point->ops with something more imperative...
  ;; Like `loop`?
  ; "generating grid history"
  ; "Elapsed time: 19782.124626 msecs"
  ; "reducing grid history"
  ; "Elapsed time: 210.257396 msecs"
  ; 543903

  ;; No real difference. Hmm.

  ;; I suppose another thing is, why do two separate reductions? (First to produce the 'grid history', then apply it.)
  ;; Why not reduce once over the _instructions_, producing an updated grid as we go?

  (time (solve' input :part-1))
  ;;="Elapsed time: 13672.229439 msecs"
  ;543903
  (time (solve' input :part-2))
  ;;="Elapsed time: 13893.981247 msecs"
  ;14687245

  ;; Not bad! Another 40% or so off!

  ;; ...
  ;; But hold up. How about flipping this thing on its head and going _light by light_? Because each light only cares
  ;; that its last op was 'turn on', or a 'toggle' from false. So maybe we can avoid running through most history?

  ;; Hmm. For part 1 anyway. Something like this:
  (time
    (let [in-range? (fn [x y x1 y1 x2 y2]
                      (and (or (<= x1 x x2) (<= x2 x x1))
                           (or (<= y1 y y2) (<= y2 y y1))))
          instructions (mapv #(let [[_ op & coords] (re-find #"([\w| ]+) (\d+),(\d+) through (\d+),(\d+)" %)]
                                (into [op] (map parse-long coords)))
                             input)
          f (fn [x y]
              (loop [instructions instructions
                     toggles-n 0]
                (let [[op x1 y1 x2 y2] (peek instructions)]
                  (if (in-range? x y x1 y1 x2 y2)
                    (case op
                      "turn on"    (if (even? toggles-n) 1 0)
                      "toggle"     (recur (pop instructions) (inc toggles-n))
                      #_"turn off" (if (odd? toggles-n) 1 0))
                    (if (seq (pop instructions))
                      (recur (pop instructions) toggles-n)
                      (if (odd? toggles-n) 1 0))))))]
      (loop [x 0
             y 0
             lit 0.0]
        (cond
          (= 1000 y) lit
          (= 999 x)  (recur 0 (inc y) (+ lit (f x y)))
          :else      (recur (inc x) y (+ lit (f x y)))))))
  ;;="Elapsed time: 7831.740248 msecs"
  ;543903

  ;; Another ~half off! Not terrible. And more than good enough for the time being, ha...

  )
