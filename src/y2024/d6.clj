(ns y2024.d6
  (:require
    [clojure.core.matrix :as m]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]
    [quil.core :as q]
    [quil.middleware :as qm]))

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

; ---

(def guard? #{\^ \> \v \<})

(defn step-or-turn
  [grid guard-dir guard-pos]
  (let [[row col] (u/dir->x+y guard-dir)
        [row' col']   (m/add guard-pos [row col])]
    (if (= (get-in grid [col' row']) \#)
      ; Turn
      [(u/turn-right guard-dir) guard-pos]
      ; Step
      [guard-dir [row' col']])))

(defn out-of-bounds?
  [grid [row col]]
  (or (< row 0) (>= row (count grid))
      (< col 0) (>= col (count (first grid)))))

(do (defn solve [grid]
  (let [[col row] (doto (u/pos-in-grid grid guard?) prn)
        start-dir (case (get-in grid [row col]) \^ :n, \> :e, \v :s, :w \<)]
    (reduce
      (fn [{:keys [dir pos visited] :as m} _]
        (if (out-of-bounds? grid pos)
          (reduced (dec ; account for out-of-bounds pos
                     (count visited)))
          (let [[dir' pos'] (step-or-turn grid dir pos)]
            (-> m
                (assoc :dir dir', :pos pos')
                (update :visited conj pos')))))
      {:dir     start-dir
       :pos     [col row]
       :visited #{[col row]}}
      (range))))

    (solve dummy-input))

(comment
  (u/pos-in-grid dummy-input guard?)
  )

(defn draw-grid

  )

(defn setup
  []
  (q/background 0)
  (q/frame-rate 30)

  (let [grid dummy-input
        [col row :as pos] (u/pos-in-grid grid guard?)]
    {:grid    grid
     :pos     pos
     :dir     (case (get-in grid [col row]) \^ :n, \> :e, \v :s, \< :w)
     :visited #{pos}}))

(defn update-state
  [{:keys [grid pos dir visited] :as state}]
  (def state state)
  (-> state
      #_(assoc :dir :n)))

(defn prnq
  [& args]
  (q/with-fill [0]
    (q/text (apply pr-str args) 480 490)))

; Apparently need to define this in fn mode, else after an error pause another error fires: arity error for this fn!
(defn key-pressed
  [state event]
  (def event event)
  (if (= :space (:key event))
    (let [[new-dir new-pos] (step-or-turn (:grid state) (:dir state) (:pos state))]
      (-> state
          (assoc :dir new-dir)
          (assoc :pos new-pos)))
    state))

(defn tri-coords
  [w h dir]
  (get {:n [10 (- h 10), (- w 10) (- h 10), (/ w 2) 10]
        :e [10 10, (- w 10) (/ h 2), 10 (- h 10)]
        :s [10 10, (- w 10) 10, (/ w 2) (- h 10)]
        :w [(- w 10) (- h 10), 10 (/ h 2), (- w 10) 10]}
       dir))

(defn draw-cell!
  [grid [row col] dir]
  (let [[cell-w cell-h] [(/ (q/width) (count grid)) (/ (q/height) (count (first grid)))]
        ch (get-in grid [row col])
        [x y w h] [(* col cell-w) (* row cell-h) cell-w cell-h]
        rect! #(q/rect x y w h)
        tri! #(q/with-translation [x y]
                (apply q/triangle (tri-coords w h dir)))]
    (q/stroke 0)
    (q/stroke-weight 2)

    (case ch
      (\^ \> \v \<)
      (do (q/fill 255)
          (rect!)
          (q/fill 200 0 0)
          (tri!))

      \#
      (do (q/fill 0)
          (rect!))

      \.
      (do (q/fill 255)
          (rect!))
      )

    ;(q/rect (* col cell-w) (* row cell-h) cell-w cell-h)

    ))

(defn draw-state
  [{:keys [grid pos dir visited] :as _state}]
  (let [
        ]

    (def grid grid)
    (doall
      (for [col-i (range (count (first grid)))
            row-i (range (count grid))]
        (do (def row-i row-i)
            (def col-i col-i)
            (def dir dir)
            (draw-cell! grid [row-i col-i] dir))))



    (prnq pos)



    ))

(defn create-sketch []
  (q/sketch
    :title "y2404.d6"
    :setup #'setup
    :draw #'draw-state
    :update #'update-state
    :key-pressed #'key-pressed
    :size [800 800]
    :middleware [qm/fun-mode qm/pause-on-error]
    :features [#_:no-safe-fns :keep-on-top]))

(def sketch (create-sketch))



(tests

)

(comment
  (solve input)


  q/defsketch


  )
