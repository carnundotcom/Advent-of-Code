(ns y2022.d8
  (:require [clojure.set :as set]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["30373"
                  "25512"
                  "65332"
                  "33549"
                  "35390"])
(def input (u/input))

; ---

(defn parse-grid [input]
  (mapv (fn [row]
          (mapv (comp parse-long str) row))
        input))

(defn- visible-this-run? [grid run]
  (:visible?
    (reduce (fn [{:keys [_visible? max-seen] :as m} [row col]]
              (let [h (get-in grid [row col])]
                (cond-> m
                  (or (nil? max-seen) (> h max-seen)) (assoc :max-seen h)
                  (or (nil? max-seen) (> h max-seen)) (update :visible? conj [row col]))))
            {:visible? #{}
             :max-seen nil}
            run)))

(defn visible-coords [grid]
  (let [coords (for [row (range (count grid))
                     col (range (count (first grid)))]
                 [row col])
        all-runs (let [left-to-right-run (partition (count grid) coords)
                       top-to-bottom-run (apply (partial map vector) left-to-right-run)]
                   [left-to-right-run
                    (map reverse left-to-right-run)
                    top-to-bottom-run
                    (map reverse top-to-bottom-run)])]
    (reduce (fn [visible? runs]
              (set/union visible?
                         (reduce (fn [visible? run]
                                   (set/union visible? (visible-this-run? grid run)))
                                 #{} runs)))
            #{} all-runs)))

(defn part-1 [input]
  (-> (parse-grid input)
      visible-coords
      count))

; ---

(defn- maybe-update-score [grid row col score]
  (let [all-runs [(for [col' (range (inc col) (count (first grid)))] [row col']) ; looking right
                  (for [col' (reverse (range 0 col))] [row col'])                ; looking left
                  (for [row' (range (inc row) (count grid))] [row' col])         ; looking down
                  (for [row' (reverse (range 0 row))] [row' col])]               ; looking up
        lookout-height (get-in grid [row col])]
    (->> all-runs
         (map (fn [run]
                (reduce (fn [score [row col]]
                          (let [h (get-in grid [row col])]
                            (cond-> score
                              :always inc
                              (<= lookout-height h) reduced)))
                        0 run)))
         (apply *)
         (max score))))

(defn max-scenic-score [grid]
  (loop [row 0, col 0
         score 0]
    (cond
      (and (= row (dec (count grid))) (= col (dec (count (first grid))))) score
      (= col (count (first grid))) (recur (inc row) 0 score)
      :else (recur row (inc col) (maybe-update-score grid row col score)))))

(defn part-2 [input]
  (->> (parse-grid input)
       max-scenic-score))

; ---

(tests
  (part-1 dummy-input) := 21
  (part-1 dummy-input) := 8)

(comment
  (part-1 input) ; => 1805
  (part-2 input) ; => 444528
  )
