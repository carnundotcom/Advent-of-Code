(ns y2022.d12
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["Sabqponm"
                  "abcryxxl"
                  "accszExk"
                  "acctuvwj"
                  "abdefghi"])
(def input (u/input))

; ---

(defn parse-nodes [input]
  (let [entries (->> (map-indexed (fn [x row]
                                    (map-indexed (fn [y c]
                                                   (cond-> [[x y] (- (int (case c, \S \a, \E \z, c)) 96)]
                                                     (= \S c) (with-meta {:S true})
                                                     (= \E c) (with-meta {:E true})))
                                                 row))
                                  input)
                     (apply concat))]
    {:S (some #(when (:S (meta %)) (first %)) entries)
     :E (some #(when (:E (meta %)) (first %)) entries)
     :nodes (into {} entries)}))

(tests
  (parse-nodes dummy-input) := {:S [0 0]
                                :E [2 5]
                                :nodes {[4 3] 5, [2 2] 3, [0 0] 1, [1 0] 1, [2 3] 19, [2 5] 26, [0 6] 14, [3 3] 20, [1 1] 2, [0 5] 15, [3 4] 21, [4 2] 4, [3 0] 1, [4 7] 9, [4 1] 2, [4 6] 8, [1 4] 25, [1 3] 18, [1 5] 24, [1 7] 12, [0 3] 17, [0 7] 13, [2 7] 11, [2 4] 26, [3 6] 23, [4 5] 7, [0 2] 2, [2 0] 1, [0 4] 16, [3 1] 3, [2 1] 3, [1 6] 24, [4 4] 6, [3 7] 10, [2 6] 24, [1 2] 3, [3 5] 22, [3 2] 3, [0 1] 1, [4 0] 1}})

(defn neighbours
  "List of [row col]s reachable from current [row col]."
  [nodes [row col]]
  (for [x '(-1 0 1), y '(-1 0 1)
        :when (and (not= (abs x) (abs y))
                   (get nodes [(+ row x) (+ col y)])
                   (<= (get nodes [(+ row x) (+ col y)])
                       (inc (get nodes [row col]))))]
    [(+ row x) (+ col y)]))

(defn- next-node [distances to-visit]
  (->> (keep (fn [node] (when-let [d (get distances node ##Inf)] [node d])) to-visit)
       (sort-by second)
       ffirst))

(defn dijkstra [nodes [row col :as _source]]
  (prn "S" [row col])
  (time
    (loop [distances {[row col] 0}
           to-visit (set (keys nodes))]
      (if (empty? to-visit)
        distances
        (let [next-node (next-node distances to-visit)]
          (when (get distances next-node)
            (let [alt (inc (get distances next-node))
                  to-visit (disj to-visit next-node)
                  distances (reduce (fn [distances neighbour]
                                      (if (< alt (get distances neighbour ##Inf))
                                        (assoc distances neighbour alt)
                                        distances))
                                    distances
                                    (filter to-visit (neighbours nodes next-node)))] (recur distances to-visit))))))))

; ---

(defn part-1 [input]
  (let [{:keys [S E nodes]} (parse-nodes input)]
    (-> (dijkstra nodes S)
        (get E))))

(defn part-2 [input]
  (let [{:keys [E nodes]} (parse-nodes input)
        starts (keep (fn [[n v]] (when (= 1 v) n)) nodes)]
    (->> (keep #(get (dijkstra nodes %) E) starts)
         (apply min))))

; ---

(tests
  (part-1 dummy-input) := 31
  (part-2 dummy-input) := 29)

(comment
  (part-1 input) ; => 383
  (part-2 input) ; => 377
  )
