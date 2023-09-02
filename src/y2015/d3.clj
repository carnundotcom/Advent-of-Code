(ns y2015.d3
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def input (first (u/input)))

; ---

(defn deliver-presents [input & {:keys [part] :or {part 1}}]
  (reduce (fn [{[xs ys] :pos-s, [xr yr] :pos-r
                turn :turn
                :as m}
               move]
            (let [[x y] (if (or (= 1 part) (= :s turn))
                          [xs ys]
                          [xr yr])
                  pos' (case move
                         \^ [x (inc y)]
                         \v [x (dec y)]
                         \> [(inc x) y]
                         \< [(dec x) y])]
              (cond-> m
                (or (= 1 part) (= :s turn)) (assoc :pos-s pos')
                (or (= 1 part) (= :s turn)) (update :visits-s conj pos')
                (= :r turn) (assoc :pos-r pos')
                (= :r turn) (update :visits-r conj pos')
                (= 2 part) (update :turn {:s :r, :r :s}))))
          {:pos-s [0 0] ; santa pos
           :visits-s #{[0 0]} ; santa visits
           :pos-r [0 0] ; robot santa pos
           :visits-r #{[0 0]} ; robot santa visits
           :turn :s} ; start on santa's turn
          input))


(tests
  ;; part 1
  (select-keys (deliver-presents ">") [:pos-s :visits-s]) := {:pos-s [1 0], :visits-s #{[0 0] [1 0]}}
  (select-keys (deliver-presents "^>v<") [:pos-s :visits-s]) := {:pos-s [0 0], :visits-s #{[0 0] [0 1] [1 1] [1 0]}}
  (select-keys (deliver-presents "^v^v^v^v^v") [:pos-s :visits-s]) := {:pos-s [0 0], :visits-s #{[0 0] [0 1]}}

  ;; part 2
  (deliver-presents "^v" {:part 2}) := {:pos-s [0 1], :visits-s #{[0 0] [0 1]}
                                        :pos-r [0 -1], :visits-r #{[0 0] [0 -1]}
                                        :turn :s}
  (deliver-presents "^>v<" {:part 2}) := {:pos-s [0 0], :visits-s #{[0 0] [0 1]}
                                          :pos-r [0 0], :visits-r #{[0 0] [1 0]}
                                          :turn :s}
  (deliver-presents "^v^v^v^v^v" {:part 2}) := {:pos-s [0 5], :visits-s #{[0 0] [0 1] [0 2] [0 3] [0 4] [0 5]}
                                                :pos-r [0 -5], :visits-r #{[0 0] [0 -1] [0 -2] [0 -3] [0 -4] [0 -5]}
                                                :turn :s}
  )

(comment
  ;; part-1
  (count (:visits-s (deliver-presents input))) ; => 2592

  ;; part-2
  (let [{:keys [visits-s visits-r]} (deliver-presents input {:part 2})]
    (count (into visits-s visits-r)))
  ; => 2360
  )
